package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class HeaderIndex(
    val status: Int,
    val id: Int,
    val results: Int = 0,
    val parent_id: Int? = null
)
