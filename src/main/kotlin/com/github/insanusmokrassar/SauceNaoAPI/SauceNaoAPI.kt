package com.github.insanusmokrassar.SauceNaoAPI

import com.github.insanusmokrassar.SauceNaoAPI.exceptions.TooManyRequestsException
import com.github.insanusmokrassar.SauceNaoAPI.exceptions.sauceNaoAPIException
import com.github.insanusmokrassar.SauceNaoAPI.models.SauceNaoAnswer
import com.github.insanusmokrassar.SauceNaoAPI.models.SauceNaoAnswerSerializer
import com.github.insanusmokrassar.SauceNaoAPI.utils.*
import io.ktor.client.HttpClient
import io.ktor.client.call.UnsupportedContentTypeException
import io.ktor.client.call.call
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.response.readText
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.io.core.*
import kotlinx.serialization.json.Json
import java.util.logging.Logger
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

data class SauceNaoAPI(
    private val apiToken: String,
    private val outputType: OutputType = JsonOutputType,
    private val client: HttpClient = HttpClient(OkHttp),
    private val searchUrl: String = SEARCH_URL,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Closeable {
    private val logger = Logger.getLogger("SauceNaoAPI")

    private val requestsChannel = Channel<Pair<Continuation<SauceNaoAnswer>, HttpRequestBuilder>>(Channel.UNLIMITED)
    private val timeManager = TimeManager(scope)
    private val quotaManager = RequestQuotaManager(scope)

    private val requestsJob = scope.launch {
        for ((callback, requestBuilder) in requestsChannel) {
            quotaManager.getQuota()
            launch {
                try {
                    val answer = makeRequest(requestBuilder)
                    callback.resumeWith(Result.success(answer))

                    quotaManager.updateQuota(answer.header, timeManager)
                } catch (e: TooManyRequestsException) {
                    quotaManager.happenTooManyRequests(timeManager)
                    requestsChannel.send(callback to requestBuilder)
                } catch (e: Exception) {
                    try {
                        callback.resumeWith(Result.failure(e))
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
        mimeType: ContentType = mediaInput.mimeType,
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
            val call = client.call(builder)
            val answerText = call.response.readText()
            logger.info(answerText)
            timeManager.addTimeAndClear()
            Json.nonstrict.parse(
                SauceNaoAnswerSerializer,
                answerText
            )
        } catch (e: ClientRequestException) {
            throw e.sauceNaoAPIException
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

                    parameter(API_TOKEN_FIELD, apiToken)
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