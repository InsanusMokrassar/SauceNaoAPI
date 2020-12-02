package dev.inmo.SauceNaoAPI.additional

import com.insanusmokrassar.SauceNaoAPI.additional.header.ResultMetaInfo
import com.insanusmokrassar.SauceNaoAPI.additional.header.adapted
import com.insanusmokrassar.SauceNaoAPI.additional.results.AdaptedResult
import com.insanusmokrassar.SauceNaoAPI.additional.results.adapted
import com.insanusmokrassar.SauceNaoAPI.models.SauceNaoAnswer

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
