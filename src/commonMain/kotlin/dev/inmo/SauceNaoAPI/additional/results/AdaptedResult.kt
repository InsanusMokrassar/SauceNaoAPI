package dev.inmo.SauceNaoAPI.additional.results

import com.insanusmokrassar.SauceNaoAPI.additional.header.IndexInfo
import com.insanusmokrassar.SauceNaoAPI.additional.header.ResultMetaInfo
import com.insanusmokrassar.SauceNaoAPI.models.Result
import com.insanusmokrassar.SauceNaoAPI.models.ResultData

fun Result.adapted(
    resultMetaInfo: ResultMetaInfo
): AdaptedResult = AdaptedResult(
    ResultHeader(
        header.similarity,
        header.thumbnail,
        resultMetaInfo.resultsInfo.indexesInfo.firstOrNull { it.id == header.indexId } ?: IndexInfo(header.indexId)
    ),
    data
)

data class AdaptedResult(
    val resultHeader: ResultHeader,
    val resultData: ResultData
)
