package dev.inmo.saucenaoapi.additional

import dev.inmo.saucenaoapi.additional.header.ResultMetaInfo
import dev.inmo.saucenaoapi.additional.header.adapted
import dev.inmo.saucenaoapi.additional.results.AdaptedResult
import dev.inmo.saucenaoapi.additional.results.adapted
import dev.inmo.saucenaoapi.models.SauceNaoAnswer

val SauceNaoAnswer.adapted: AdaptedAnswer
    get() = header.adapted.let { resultMetainfo ->
        val adaptedResults = results.map {
            it.adapted(resultMetainfo)
        }
        AdaptedAnswer(
            resultMetainfo,
            adaptedResults
        )
    }

data class AdaptedAnswer(
    val resultMetaInfo: ResultMetaInfo,
    val results: List<AdaptedResult>
)
