package dev.inmo.SauceNaoAPI.utils

import com.insanusmokrassar.SauceNaoAPI.additional.LONG_TIME_RECALCULATING_MILLIS
import com.insanusmokrassar.SauceNaoAPI.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.insanusmokrassar.SauceNaoAPI.models.Header
import com.soywiz.klock.DateTime

internal suspend fun calculateSleepTime(
    header: Header,
    mostOldestInShortPeriodGetter: suspend () -> DateTime?,
    mostOldestInLongPeriodGetter: suspend () -> DateTime?
): DateTime? {
    return when {
        header.longRemaining < 1 -> mostOldestInLongPeriodGetter() ?.plus(LONG_TIME_RECALCULATING_MILLIS)
        header.shortRemaining < 1 -> mostOldestInShortPeriodGetter() ?.plus(SHORT_TIME_RECALCULATING_MILLIS)
        else -> null
    }
}