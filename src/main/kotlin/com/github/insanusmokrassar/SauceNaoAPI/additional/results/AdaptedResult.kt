package com.github.insanusmokrassar.SauceNaoAPI.additional.results

import com.github.insanusmokrassar.SauceNaoAPI.additional.header.IndexInfo
import com.github.insanusmokrassar.SauceNaoAPI.additional.header.ResultMetaInfo
import com.github.insanusmokrassar.SauceNaoAPI.models.Result
import com.github.insanusmokrassar.SauceNaoAPI.models.ResultData

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
