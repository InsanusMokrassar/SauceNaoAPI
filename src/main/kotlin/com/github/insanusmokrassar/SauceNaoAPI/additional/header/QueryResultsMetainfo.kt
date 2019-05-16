package com.github.insanusmokrassar.SauceNaoAPI.additional.header

import com.github.insanusmokrassar.SauceNaoAPI.models.Header

val Header.queryPreview
    get() = QueryResultPreview(
        queryImageDisplay,
        queryImage
    )

val Header.queryResults
    get() = QueryResultsMetainfo(
        status ?: 500, // server_side error if status field was not set up
        resultsCount ?: 0,
        minSimilarity ?: 0F,
        searchDepth ?: 128,
        queryPreview,
        adaptedIndexes
    )

data class QueryResultPreview(
    val imageDisplay: String? = null, // something like "userdata/uuid.png",
    val image: String? = null // something like "uuid.jpg"
)

data class QueryResultsMetainfo(
    val status: Int = 0,
    val count: Int = 0,
    val minSimilarity: Float = 0F,
    val searchDepth: Int = 128,
    val preview: QueryResultPreview = QueryResultPreview(),
    val indexesInfo: List<IndexInfo> = emptyList()
) {
    val isOk: Boolean = status == 0
    val isClientSideError = status < 0
    val isServerSideError = status > 0
}
