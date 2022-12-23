package dev.inmo.saucenaoapi.models

import kotlinx.serialization.Serializable

@Serializable
data class LimitsState(
    val maxShortQuota: Int,
    val maxLongQuota: Int,
    val knownShortQuota: Int,
    val knownLongQuota: Int
) : Comparable<LimitsState> {
    override fun compareTo(other: LimitsState): Int = knownShortQuota.compareTo(other.knownShortQuota)
}
