package dev.inmo.SauceNaoAPI.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultHeader(
    val similarity: Float,
    val thumbnail: String,
    @SerialName("index_id")
    val indexId: Int,
    @SerialName("index_name")
    val indexName: String
)