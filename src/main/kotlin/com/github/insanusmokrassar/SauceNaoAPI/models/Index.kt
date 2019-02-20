package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class Index(
    val status: Int,
    val parent_id: Int,
    val id: Int,
    val results: Int
)
