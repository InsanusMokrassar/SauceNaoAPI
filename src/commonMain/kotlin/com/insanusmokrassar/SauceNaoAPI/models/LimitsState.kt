package com.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.Serializable

@Serializable
data class LimitsState(
    val maxShortQuota: Int,
    val maxLongQuota: Int,
    val knownShortQuota: Int,
    val knownLongQuota: Int
)
