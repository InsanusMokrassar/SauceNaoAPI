package com.github.insanusmokrassar.SauceNaoAPI.utils

import com.github.insanusmokrassar.SauceNaoAPI.additional.LONG_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.exceptions.TooManyRequestsException
import com.github.insanusmokrassar.SauceNaoAPI.exceptions.TooManyRequestsLongException
import com.github.insanusmokrassar.SauceNaoAPI.models.Header
import com.github.insanusmokrassar.SauceNaoAPI.models.LimitsState
import com.soywiz.klock.DateTime
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.Closeable
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.min

internal class RequestQuotaManager (
    scope: CoroutineScope
) : Closeable {
    private var longQuota = 1
    private var shortQuota = 1
    private var longMaxQuota = 1
    private var shortMaxQuota = 1

    val limitsState: LimitsState
        get() = LimitsState(
            shortMaxQuota,
            longMaxQuota,
            shortQuota,
            longQuota
        )

    private val quotaActions = Channel<suspend () -> Unit>(Channel.UNLIMITED)

    private val quotaJob = scope.launch {
        for (callback in quotaActions) {
            callback()
        }
    }

    private suspend fun updateQuota(
        newLongQuota: Int,
        newShortQuota: Int,
        newMaxLongQuota: Int?,
        newMaxShortQuota: Int?,
        timeManager: TimeManager
    ) {
        quotaActions.send(
            suspend {
                longMaxQuota = newMaxLongQuota ?: longMaxQuota
                shortMaxQuota = newMaxShortQuota ?: shortMaxQuota

                longQuota = min(newLongQuota, longMaxQuota)
                shortQuota = min(newShortQuota, shortMaxQuota)

                when {
                    longQuota < 1 -> (timeManager.getMostOldestInLongPeriod() ?: DateTime.now()) + LONG_TIME_RECALCULATING_MILLIS
                    shortQuota < 1 -> (timeManager.getMostOldestInShortPeriod() ?: DateTime.now()) + SHORT_TIME_RECALCULATING_MILLIS
                    else -> null
                } ?.also {
                    delay((it - DateTime.now()).millisecondsLong)
                    shortQuota = max(shortQuota, 1)
                    longQuota = max(longQuota, 1)
                }
                Unit
            }
        )
    }

    suspend fun updateQuota(header: Header, timeManager: TimeManager) = updateQuota(
        header.longRemaining,
        header.shortRemaining,
        header.longLimit,
        header.shortLimit,
        timeManager
    )

    suspend fun happenTooManyRequests(timeManager: TimeManager, e: TooManyRequestsException) = updateQuota(
        if (e is TooManyRequestsLongException) 0 else 1,
        0,
        null,
        null,
        timeManager
    )

    suspend fun getQuota() {
        return suspendCoroutine {
            lateinit var callback: suspend () -> Unit
            callback = suspend {
                if (longQuota > 0 && shortQuota > 0) {
                    it.resumeWith(Result.success(Unit))
                } else {
                    quotaActions.send(callback)
                }
            }
            quotaActions.offer(callback)
        }
    }

    override fun close() {
        quotaJob.cancel()
        quotaActions.close()
    }
}
