package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

@Serializable
data class HeaderIndex(
    val status: Int,
    val id: Int,
    @Optional
    val results: Int = 0,
    @Optional
    val parent_id: Int? = null
)
