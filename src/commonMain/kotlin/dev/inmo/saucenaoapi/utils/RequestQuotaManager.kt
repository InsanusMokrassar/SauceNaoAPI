package dev.inmo.saucenaoapi.utils

import com.soywiz.klock.DateTime
import dev.inmo.saucenaoapi.additional.LONG_TIME_RECALCULATING_MILLIS
import dev.inmo.saucenaoapi.additional.SHORT_TIME_RECALCULATING_MILLIS
import dev.inmo.saucenaoapi.exceptions.TooManyRequestsException
import dev.inmo.saucenaoapi.exceptions.TooManyRequestsLongException
import dev.inmo.saucenaoapi.models.Header
import dev.inmo.saucenaoapi.models.LimitsState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.math.max
import kotlin.math.min

internal class RequestQuotaManager (
    scope: CoroutineScope
) {
    private val longQuotaFlow = MutableStateFlow(1)
    private val shortQuotaFlow = MutableStateFlow(1)
    private val longMaxQuotaFlow = MutableStateFlow(1)
    private val shortMaxQuotaFlow = MutableStateFlow(1)
    private var longQuota by longQuotaFlow::value
    private var shortQuota by shortQuotaFlow::value
    private var longMaxQuota by longMaxQuotaFlow::value
    private var shortMaxQuota by shortMaxQuotaFlow::value

    val limitsStateFlow = merge(
        longQuotaFlow, shortQuotaFlow, longMaxQuotaFlow, shortMaxQuotaFlow
    ).map { _ ->
        LimitsState(
            shortMaxQuota,
            longMaxQuota,
            shortQuota,
            longQuota
        )
    }
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
    }.also {
        it.invokeOnCompletion {
            quotaActions.close(it)
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
        val job = Job()
        lateinit var callback: suspend () -> Unit
        callback = suspend {
            if (longQuota > 0 && shortQuota > 0) {
                job.complete()
            } else {
                quotaActions.send(callback)
            }
        }
        quotaActions.trySend(callback)
        return job.join()
    }
}
