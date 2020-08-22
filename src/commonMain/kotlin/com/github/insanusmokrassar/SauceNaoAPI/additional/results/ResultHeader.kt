package com.github.insanusmokrassar.SauceNaoAPI.additional.results

import com.github.insanusmokrassar.SauceNaoAPI.additional.header.IndexInfo

data class ResultHeader(
    val similarity: Float,
    val thumbnail: String,
    val index: IndexInfo
)
