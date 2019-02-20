package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Header(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("account_type")
    val accountType: String,
    @SerialName("short_limit")
    val shortLimit: Int,
    @SerialName("long_limit")
    val longLimit: Int,
    @SerialName("short_remaining")
    val shortRemaining: Int,
    @SerialName("long_remaining")
    val longRemaining: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("results_requested")
    val resultsRequested: Int,
    @SerialName("index")
    val indexes: List<Index>,
    @SerialName("search_depth")
    val searchDepth: Int,
    @SerialName("minimum_similarity")
    val minSimilarity: Float,
    @SerialName("query_image_display")
    val queryImageDisplay: String, // something like "userdata/uuid.png",
    @SerialName("query_image")
    val queryImage: String, // something like "uuid.jpg"
    @SerialName("results_returned")
    val resultsCount: Int
)
