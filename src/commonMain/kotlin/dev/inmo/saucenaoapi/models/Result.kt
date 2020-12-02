package dev.inmo.saucenaoapi.models

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val header: ResultHeader,
    val data: ResultData
)
