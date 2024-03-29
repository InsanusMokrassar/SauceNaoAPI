package dev.inmo.saucenaoapi

import dev.inmo.micro_utils.common.MPPFile
import dev.inmo.micro_utils.ktor.common.input
import dev.inmo.saucenaoapi.exceptions.TooManyRequestsException
import dev.inmo.saucenaoapi.exceptions.sauceNaoAPIException
import dev.inmo.saucenaoapi.models.*
import dev.inmo.saucenaoapi.utils.*
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.utils.io.core.Input
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json

private const val API_TOKEN_FIELD = "api_key"
private const val OUTPUT_TYPE_FIELD = "output_type"
private const val URL_FIELD = "url"
private const val FILE_FIELD = "file"
private const val FILENAME_FIELD = "filename"
private const val DB_FIELD = "db"
private const val DBS_FIELD = "dbs[]"
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
    private val client: HttpClient = HttpClient(),
    private val searchUrl: String = SEARCH_URL,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val parser: Json = defaultSauceNaoParser
) : SauceCloseable {
    private val requestsChannel = Channel<Pair<CompletableDeferred<SauceNaoAnswer>, HttpRequestBuilder>>(Channel.UNLIMITED)
    private val subscope = CoroutineScope(scope.coroutineContext + SupervisorJob(scope.coroutineContext.job)).also {
        it.coroutineContext.job.invokeOnCompletion {
            requestsChannel.close(it)
        }
    }
    private val timeManager = TimeManager(subscope)
    private val quotaManager = RequestQuotaManager(subscope)

    val limitsState: LimitsState by quotaManager::limitsState
    val longQuotaFlow by quotaManager::longQuotaFlow
    val shortQuotaFlow by quotaManager::shortQuotaFlow
    val longMaxQuotaFlow by quotaManager::longMaxQuotaFlow
    val shortMaxQuotaFlow by quotaManager::shortMaxQuotaFlow
    val limitsStateFlow by quotaManager::limitsStateFlow

    private val requestsJob = subscope.launch {
        for ((callback, requestBuilder) in requestsChannel) {
            quotaManager.getQuota()
            launch {
                try {
                    val answer = makeRequest(requestBuilder)
                    callback.complete(answer)

                    quotaManager.updateQuota(answer.header, timeManager)
                } catch (e: TooManyRequestsException) {
                    quotaManager.happenTooManyRequests(timeManager, e)
                    requestsChannel.send(callback to requestBuilder)
                } catch (e: Exception) {
                    try {
                        callback.completeExceptionally(e)
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
    ): SauceNaoAnswer = makeRequest(
        url.asSauceRequestSubject,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    /**
     * @param db search a specific index number or all without needing to generate a bitmask.
     * @param dbs search one or more specific index number, set more than once to search multiple.
     */
    suspend fun requestByDBs(
        url: String,
        db: Int? = null,
        dbs: Array<Int>? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = makeRequest(
        url.asSauceRequestSubject,
        db = db,
        dbs = dbs,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    /**
     * @param mask Mask for selecting specific indexes to ENABLE. dbmask=8191 will search all of the first 14 indexes. If intending to search all databases, the db=999 option is more appropriate.
     * @param excludedMask Mask for selecting specific indexes to DISABLE. dbmaski=8191 would search only indexes higher than the first 14. This is ideal when attempting to disable only certain indexes, while allowing future indexes to be included by default.
     *
     * Bitmask Note: Index numbers start with 0. Even though pixiv is labeled as index 5, it would be controlled with the 6th bit position, which has a decimal value of 32 when set.
     * db=<index num or 999 for all>
     */
    suspend fun requestByMasks(
        url: String,
        mask: Int?,
        excludedMask: Int? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = makeRequest(
        url.asSauceRequestSubject,
        dbmask = mask,
        dbmaski = excludedMask,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    suspend fun request(
        mediaInput: Input,
        mimeType: ContentType,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = makeRequest(
        mediaInput.asSauceRequestSubject(mimeType),
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    /**
     * @param db search a specific index number or all without needing to generate a bitmask.
     * @param dbs search one or more specific index number, set more than once to search multiple.
     */
    suspend fun requestByDBs(
        mediaInput: Input,
        mimeType: ContentType,
        db: Int? = null,
        dbs: Array<Int>? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = makeRequest(
        mediaInput.asSauceRequestSubject(mimeType),
        db = db,
        dbs = dbs,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    /**
     * @param mask Mask for selecting specific indexes to ENABLE. dbmask=8191 will search all of the first 14 indexes. If intending to search all databases, the db=999 option is more appropriate.
     * @param excludedMask Mask for selecting specific indexes to DISABLE. dbmaski=8191 would search only indexes higher than the first 14. This is ideal when attempting to disable only certain indexes, while allowing future indexes to be included by default.
     *
     * Bitmask Note: Index numbers start with 0. Even though pixiv is labeled as index 5, it would be controlled with the 6th bit position, which has a decimal value of 32 when set.
     * db=<index num or 999 for all>
     */
    suspend fun requestByMasks(
        mediaInput: Input,
        mimeType: ContentType,
        mask: Int?,
        excludedMask: Int? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = makeRequest(
        mediaInput.asSauceRequestSubject(mimeType),
        dbmask = mask,
        dbmaski = excludedMask,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    suspend fun request(
        file: MPPFile,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = request(
        file.input(),
        file.contentType,
        resultsCount,
        minSimilarity
    )

    /**
     * @param db search a specific index number or all without needing to generate a bitmask.
     * @param dbs search one or more specific index number, set more than once to search multiple.
     */
    suspend fun requestByDBs(
        file: MPPFile,
        db: Int? = null,
        dbs: Array<Int>? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = requestByDBs(
        file.input(),
        file.contentType,
        db,
        dbs,
        resultsCount,
        minSimilarity
    )

    /**
     * @param mask Mask for selecting specific indexes to ENABLE. dbmask=8191 will search all of the first 14 indexes. If intending to search all databases, the db=999 option is more appropriate.
     * @param excludedMask Mask for selecting specific indexes to DISABLE. dbmaski=8191 would search only indexes higher than the first 14. This is ideal when attempting to disable only certain indexes, while allowing future indexes to be included by default.
     *
     * Bitmask Note: Index numbers start with 0. Even though pixiv is labeled as index 5, it would be controlled with the 6th bit position, which has a decimal value of 32 when set.
     * db=<index num or 999 for all>
     */
    suspend fun requestByMasks(
        file: MPPFile,
        mask: Int?,
        excludedMask: Int? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = requestByMasks(
        file.input(),
        file.contentType,
        mask,
        excludedMask,
        resultsCount,
        minSimilarity
    )

    @Deprecated("Renamed", ReplaceWith("requestByDBs(url, db, null, resultsCount, minSimilarity)"))
    suspend fun requestByDb(
        url: String,
        db: Int,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = requestByDBs(
        url,
        db,
        null,
        resultsCount,
        minSimilarity
    )

    @Deprecated("Renamed", ReplaceWith("requestByMasks(url, dbmask, null, resultsCount, minSimilarity)"))
    suspend fun request(
        url: String,
        dbmask: Int,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = requestByMasks(
        url,
        dbmask,
        null,
        resultsCount,
        minSimilarity
    )

    @Deprecated("Renamed", ReplaceWith("requestByMasks(url, null, dbmaski, resultsCount, minSimilarity)"))
    suspend fun requestByMaskI(
        url: String,
        dbmaski: Int,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer = requestByMasks(
        url,
        null,
        dbmaski,
        resultsCount,
        minSimilarity
    )

    private suspend fun makeRequest(
        builder: HttpRequestBuilder
    ): SauceNaoAnswer {
        return try {
            val call = client.request(builder)
            val answerText = call.bodyAsText()
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
        dbs: Array<Int>? = null,
        dbmask: Int? = null,
        dbmaski: Int? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer {
        val deferred = CompletableDeferred<SauceNaoAnswer>()

        requestsChannel.trySend(
            deferred to HttpRequestBuilder().apply {
                url(searchUrl)

                apiToken ?.also { parameter(API_TOKEN_FIELD, it) }
                parameter(OUTPUT_TYPE_FIELD, JsonOutputType.typeCode)
                db ?.also { parameter(DB_FIELD, it) }
                dbs ?.forEach { parameter(DBS_FIELD, it) }
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
                        setBody(
                            MultiPartFormDataContent(
                                formData {
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
                                }
                            )
                        )
                    }
                }
            }
        )

        return deferred.await()
    }

    override fun close() {
        subscope.cancel()
    }
}
