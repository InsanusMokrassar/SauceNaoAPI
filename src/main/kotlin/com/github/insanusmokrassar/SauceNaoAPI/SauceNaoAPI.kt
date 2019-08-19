package com.github.insanusmokrassar.SauceNaoAPI

import com.github.insanusmokrassar.SauceNaoAPI.exceptions.sauceNaoAPIException
import com.github.insanusmokrassar.SauceNaoAPI.models.SauceNaoAnswer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.*
import io.ktor.client.response.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.io.core.Closeable
import kotlinx.serialization.json.Json
import org.joda.time.DateTime
import java.util.logging.Logger
import kotlin.coroutines.*

private const val API_TOKEN_FIELD = "api_key"
private const val OUTPUT_TYPE_FIELD = "output_type"
private const val URL_FIELD = "url"
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
    private val searchUrl: String = SEARCH_URL
) : Closeable {
    private val logger = Logger.getLogger("SauceNaoAPI")

    private val requestsChannel = Channel<Pair<Continuation<SauceNaoAnswer>, HttpRequestBuilder>>(Channel.UNLIMITED)
    private val requestsSendTimes = mutableListOf<DateTime>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            for ((callback, requestBuilder) in requestsChannel) {
                try {
                    val answer = makeRequest(requestBuilder)
                    callback.resumeWith(Result.success(answer))

                    val sleepUntil = if (answer.header.longRemaining == 0) {
                        getMostOldestInLongPeriod() ?.plusMillis(LONG_TIME_LIMIT_MILLIS)
                    } else {
                        if (answer.header.shortRemaining == 0) {
                            getMostOldestInShortPeriod() ?.plusMillis(SHORT_TIME_LIMIT_MILLIS)
                        } else {
                            null
                        }
                    }

                    sleepUntil ?.also { _ ->
                        logger.warning("LONG LIMIT REACHED, SLEEP UNTIL $sleepUntil")
                        delay(sleepUntil.millis - DateTime.now().millis)
                    }
                } catch (e: Exception) {
                    callback.resumeWith(Result.failure(e))
                }
            }
        }
    }

    suspend fun request(
        url: String,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? = makeRequest(
        url,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    suspend fun requestByDb(
        url: String,
        db: Int,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? = makeRequest(
        url,
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
        url,
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
        url,
        dbmaski = dbmaski,
        resultsCount = resultsCount,
        minSimilarity = minSimilarity
    )

    private suspend fun makeRequest(
        builder: HttpRequestBuilder
    ): SauceNaoAnswer {
        return try {
            val call = client.execute(builder)
            val answerText = call.response.readText()
            logger.info(answerText)
            addRequestTimesAndClear()
            Json.nonstrict.parse(
                SauceNaoAnswer.serializer(),
                answerText
            )
        } catch (e: ClientRequestException) {
            throw e.sauceNaoAPIException
        }
    }

    private fun addRequestTimesAndClear() {
        val newDateTime = DateTime.now()

        clearRequestTimes(newDateTime)

        requestsSendTimes.add(newDateTime)
    }

    private fun clearRequestTimes(relatedTo: DateTime = DateTime.now()) {
        val limitValue = relatedTo.minusMillis(LONG_TIME_LIMIT_MILLIS)

        requestsSendTimes.removeAll {
            it < limitValue
        }
    }

    private fun getMostOldestInLongPeriod(): DateTime? {
        clearRequestTimes()

        return requestsSendTimes.min()
    }

    private fun getMostOldestInShortPeriod(): DateTime? {
        val now = DateTime.now()

        val limitTime = now.minusMillis(SHORT_TIME_LIMIT_MILLIS)

        clearRequestTimes(now)

        return requestsSendTimes.asSequence().filter {
            limitTime < it
        }.min()
    }

    private suspend fun makeRequest(
        url: String,
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
                    parameter(URL_FIELD, url)
                    parameter(API_TOKEN_FIELD, apiToken)
                    parameter(OUTPUT_TYPE_FIELD, outputType.typeCode)
                    db ?.also { parameter(DB_FIELD, it) }
                    dbmask ?.also { parameter(DBMASK_FIELD, it) }
                    dbmaski ?.also { parameter(DBMASKI_FIELD, it) }
                    resultsCount ?.also { parameter(RESULTS_COUNT_FIELD, it) }
                    minSimilarity ?.also { parameter(MINIMAL_SIMILARITY_FIELD, it) }
                }
            )
        }
    }

    override fun close() {
        requestsChannel.close()
        requestsSendTimes.clear()
        client.close()
    }
}