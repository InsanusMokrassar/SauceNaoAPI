package com.github.insanusmokrassar.SauceNaoAPI.utils

import com.github.insanusmokrassar.SauceNaoAPI.additional.LONG_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.models.Header
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan

internal suspend fun calculateSleepTime(
    header: Header,
    mostOldestInShortPeriodGetter: suspend () -> DateTime?,
    mostOldestInLongPeriodGetter: suspend () -> DateTime?
): DateTime? {
    return when {
        header.longRemaining < 1 -> mostOldestInLongPeriodGetter() ?.plus(TimeSpan(LONG_TIME_RECALCULATING_MILLIS))
        header.shortRemaining < 1 -> mostOldestInShortPeriodGetter() ?.plus(TimeSpan(SHORT_TIME_RECALCULATING_MILLIS))
        else -> null
    }
}