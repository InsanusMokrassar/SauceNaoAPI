package dev.inmo.saucenaoapi.additional.results

import dev.inmo.saucenaoapi.additional.header.IndexInfo
import dev.inmo.saucenaoapi.additional.header.ResultMetaInfo
import dev.inmo.saucenaoapi.models.Result
import dev.inmo.saucenaoapi.models.ResultData

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
