package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val header: Header,
    val data: ResultData
)
