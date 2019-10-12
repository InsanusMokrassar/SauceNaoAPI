package com.github.insanusmokrassar.SauceNaoAPI.utils

import com.github.insanusmokrassar.SauceNaoAPI.additional.LONG_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.models.Header
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.io.core.Closeable
import org.joda.time.DateTime
import kotlin.coroutines.suspendCoroutine
import kotlin.math.min

class RequestQuotaManager (
    private val scope: CoroutineScope
) : Closeable {
    private var longQuota = 1
    private var shortQuota = 1
    private var longMaxQuota = 1
    private var shortMaxQuota = 1

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
                    shortQuota < 1 -> timeManager.getMostOldestInShortPeriod() ?.millis ?.plus(SHORT_TIME_RECALCULATING_MILLIS) ?: let {
                        shortQuota = 1
                        null
                    }
                    longQuota < 1 -> timeManager.getMostOldestInLongPeriod() ?.millis ?.plus(LONG_TIME_RECALCULATING_MILLIS) ?: let {
                        longQuota = 1
                        null
                    }
                    else -> null
                } ?.let {
                    delay(it - DateTime.now().millis)
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

    suspend fun happenTooManyRequests(timeManager: TimeManager) = updateQuota(
        1,
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
