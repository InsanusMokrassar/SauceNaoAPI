package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class SauceNaoAnswer(
    val header: Header,
    val data: ResultData
)
