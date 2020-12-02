package dev.inmo.SauceNaoAPI

import com.insanusmokrassar.SauceNaoAPI.exceptions.TooManyRequestsException
import com.insanusmokrassar.SauceNaoAPI.exceptions.sauceNaoAPIException
import com.insanusmokrassar.SauceNaoAPI.models.*
import com.insanusmokrassar.SauceNaoAPI.utils.*
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import io.ktor.utils.io.core.Input
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json
import kotlin.coroutines.*

private const val API_TOKEN_FIELD = "api_key"
private const val OUTPUT_TYPE_FIELD = "output_type"
private const val URL_FIELD = "url"
private const val FILE_FIELD = "file"
private const val FILENAME_FIELD = "filename"
private const val DB_FIELD = "db"
private const val DBMASK_FIELD = "dbmask"
private const val DBMASKI_FIELD = "dbmaski"
private const val RESULTS_COUNT_FIELD = "numres"
private const val MINIMAL_SIMILARITY_FIELD = "minsim"

private const val SEARCH_URL = "https://saucenao.com/search.php"

val defaultSauceNaoParser = Json {
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
    useArrayPolymorphism = true
}

data class SauceNaoAPI(
    private val apiToken: String? = null,
    private val outputType: dev.inmo.SauceNaoAPI.OutputType = dev.inmo.SauceNaoAPI.JsonOutputType,
    private val client: HttpClient = HttpClient(),
    private val searchUrl: String = SEARCH_URL,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val parser: Json = defaultSauceNaoParser
) : SauceCloseable {
    private val requestsChannel = Channel<Pair<Continuation<SauceNaoAnswer>, HttpRequestBuilder>>(Channel.UNLIMITED)
    private val timeManager = TimeManager(scope)
    private val quotaManager = RequestQuotaManager(scope)

    val limitsState: LimitsState
        get() = quotaManager.limitsState

    private val requestsJob = scope.launch {
        for ((callback, requestBuilder) in requestsChannel) {
            quotaManager.getQuota()
            launch {
                try {
                    val answer = makeRequest(requestBuilder)
                    callback.resume(answer)

                    quotaManager.updateQuota(answer.header, timeManager)
                } catch (e: TooManyRequestsException) {
                    quotaManager.happenTooManyRequests(timeManager, e)
                    requestsChannel.send(callback to requestBuilder)
                } catch (e: Exception) {
                    try {
                        callback.resumeWithException(e)
                    } catch (e: IllegalStateException) { // may happen when already resumed and api was closed
                        // do nothing
                    }
                }
            }
        }
    }

    suspend fun request(
        url: String,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? = makeRequest(
        url.asSauceRequestSubject,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    suspend fun request(
        mediaInput: Input,
        mimeType: ContentType,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? = makeRequest(
        mediaInput.asSauceRequestSubject(mimeType),
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    suspend fun requestByDb(
        url: String,
        db: Int,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? = makeRequest(
        url.asSauceRequestSubject,
        db = db,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    suspend fun requestByMask(
        url: String,
        dbmask: Int,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? = makeRequest(
        url.asSauceRequestSubject,
        dbmask = dbmask,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    suspend fun requestByMaskI(
        url: String,
        dbmaski: Int,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? = makeRequest(
        url.asSauceRequestSubject,
        dbmaski = dbmaski,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    private suspend fun makeRequest(
        builder: HttpRequestBuilder
    ): SauceNaoAnswer {
        return try {
            val call = client.request<HttpResponse>(builder)
            val answerText = call.readText()
            timeManager.addTimeAndClear()
            parser.decodeFromString(
                SauceNaoAnswerSerializer,
                answerText
            )
        } catch (e: ClientRequestException) {
            throw e.sauceNaoAPIException()
        }
    }

    private suspend fun makeRequest(
        request: SauceRequestSubject,
        db: Int? = null,
        dbmask: Int? = null,
        dbmaski: Int? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? {
        return suspendCoroutine<SauceNaoAnswer> {
            requestsChannel.offer(
                it to HttpRequestBuilder().apply {
                    url(searchUrl)

                    apiToken ?.also { parameter(API_TOKEN_FIELD, it) }
                    parameter(OUTPUT_TYPE_FIELD, outputType.typeCode)
                    db ?.also { parameter(DB_FIELD, it) }
                    dbmask ?.also { parameter(DBMASK_FIELD, it) }
                    dbmaski ?.also { parameter(DBMASKI_FIELD, it) }
                    resultsCount ?.also { parameter(RESULTS_COUNT_FIELD, it) }
                    minSimilarity ?.also { parameter(MINIMAL_SIMILARITY_FIELD, it) }

                    when (request) {
                        is UrlSauceRequestSubject -> {
                            parameter(URL_FIELD, request.url)
                        }
                        is InputRequestSubject -> {
                            val mimeType = request.mimeType

                            method = HttpMethod.Post
                            body = MultiPartFormDataContent(formData {
                                    appendInput(
                                        FILE_FIELD,
                                        Headers.build {
                                            append(HttpHeaders.ContentType, mimeType.toString())

                                            val fakeFilename = "filename=file" + when (mimeType) {
                                                ContentType.Image.GIF -> ".gif"
                                                ContentType.Image.JPEG -> ".jpeg"
                                                ContentType.Image.PNG -> ".png"
                                                ContentType.Image.SVG -> ".svg"
                                                else -> throw IllegalArgumentException(
                                                    "Currently supported formats for uploading in sauce: gif, jpeg, png, svg"
                                                )
                                            }
                                            append(HttpHeaders.ContentDisposition, "filename=$fakeFilename")
                                        },
                                        block = request::input
                                    )
                            })
                        }
                    }
                }
            )
        }
    }

    override fun close() {
        requestsChannel.close()
        client.close()
        requestsJob.cancel()
        timeManager.close()
        quotaManager.close()
    }
}