package dev.inmo.saucenaoapi.models

import kotlinx.serialization.Serializable

@Serializable
data class HeaderIndex(
    val status: Int? = null,
    val id: Int? = null,
    val results: Int? = null,
    val parent_id: Int? = null
)
