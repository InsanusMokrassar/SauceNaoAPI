package dev.inmo.saucenaoapi.additional.results

import dev.inmo.saucenaoapi.additional.header.IndexInfo

data class ResultHeader(
    val similarity: Float,
    val thumbnail: String,
    val index: IndexInfo
)
