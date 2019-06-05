package com.github.insanusmokrassar.SauceNaoAPI.additional

import com.github.insanusmokrassar.SauceNaoAPI.additional.header.ResultMetaInfo
import com.github.insanusmokrassar.SauceNaoAPI.additional.header.adapted
import com.github.insanusmokrassar.SauceNaoAPI.additional.results.AdaptedResult
import com.github.insanusmokrassar.SauceNaoAPI.additional.results.adapted
import com.github.insanusmokrassar.SauceNaoAPI.models.SauceNaoAnswer

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
