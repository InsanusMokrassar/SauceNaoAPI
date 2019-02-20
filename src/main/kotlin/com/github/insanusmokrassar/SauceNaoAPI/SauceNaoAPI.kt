package com.github.insanusmokrassar.SauceNaoAPI

import com.github.insanusmokrassar.SauceNaoAPI.models.SauceNaoAnswer
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.response.readText
import kotlinx.serialization.json.JSON

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
) {
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
        url: String,
        db: Int? = null,
        dbmask: Int? = null,
        dbmaski: Int? = null,
        resultsCount: Int? = null,
        minSimilarity: Float? = null
    ): SauceNaoAnswer? {
        return client.call {
            url(searchUrl)
            parameter(URL_FIELD, url)
            parameter(API_TOKEN_FIELD, apiToken)
            parameter(OUTPUT_TYPE_FIELD, outputType.typeCode)
            db ?.also { parameter(DB_FIELD, it) }
            dbmask ?.also { parameter(DBMASK_FIELD, it) }
            dbmaski ?.also { parameter(DBMASKI_FIELD, it) }
            resultsCount ?.also { parameter(RESULTS_COUNT_FIELD, it) }
            minSimilarity ?.also { parameter(MINIMAL_SIMILARITY_FIELD, it) }
        }.response.readText().let {
            JSON.nonstrict.parse(
                SauceNaoAnswer.serializer(),
                it
            )
        }
    }
}