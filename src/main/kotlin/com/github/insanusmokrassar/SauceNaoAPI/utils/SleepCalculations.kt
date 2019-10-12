package com.github.insanusmokrassar.SauceNaoAPI.utils

import com.github.insanusmokrassar.SauceNaoAPI.additional.LONG_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.models.Header
import org.joda.time.DateTime

internal suspend fun calculateSleepTime(
    header: Header,
    mostOldestInShortPeriodGetter: suspend () -> DateTime?,
    mostOldestInLongPeriodGetter: suspend () -> DateTime?
): DateTime? {
    return when {
        header.longRemaining < 1 -> mostOldestInLongPeriodGetter() ?.plusMillis(LONG_TIME_RECALCULATING_MILLIS)
        header.shortRemaining < 1 -> mostOldestInShortPeriodGetter() ?.plusMillis(SHORT_TIME_RECALCULATING_MILLIS)
        else -> null
    }
}