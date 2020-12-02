package dev.inmo.SauceNaoAPI.additional.results

import com.insanusmokrassar.SauceNaoAPI.additional.header.IndexInfo

data class ResultHeader(
    val similarity: Float,
    val thumbnail: String,
    val index: IndexInfo
)
