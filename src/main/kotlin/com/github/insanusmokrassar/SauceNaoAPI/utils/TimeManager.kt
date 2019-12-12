package com.github.insanusmokrassar.SauceNaoAPI.utils

import com.github.insanusmokrassar.SauceNaoAPI.additional.LONG_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.io.core.Closeable
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

private fun MutableList<DateTime>.clearTooOldTimes(relatedTo: DateTime = DateTime.now()) {
    val limitValue = relatedTo.minus(TimeSpan(LONG_TIME_RECALCULATING_MILLIS.toDouble()))

    removeAll {
        it < limitValue
    }
}

private interface TimeManagerAction {
    suspend fun makeChangeWith(times: MutableList<DateTime>)
    suspend operator fun invoke(times: MutableList<DateTime>) = makeChangeWith(times)
}

private data class TimeManagerClean(private val relatedTo: DateTime = DateTime.now()) : TimeManagerAction {
    override suspend fun makeChangeWith(times: MutableList<DateTime>) {
        times.clearTooOldTimes(relatedTo)
    }
}

private data class TimeManagerTimeAdder(
    private val time: DateTime = DateTime.now()
) : TimeManagerAction {
    override suspend fun makeChangeWith(times: MutableList<DateTime>) {
        times.add(time)
        times.clearTooOldTimes()
    }
}

private data class TimeManagerMostOldestInLongGetter(
    private val continuation: Continuation<DateTime?>
) : TimeManagerAction {
    override suspend fun makeChangeWith(times: MutableList<DateTime>) {
        times.clearTooOldTimes()
        continuation.resumeWith(Result.success(times.min()))
    }
}

private data class TimeManagerMostOldestInShortGetter(
    private val continuation: Continuation<DateTime?>
) : TimeManagerAction {
    override suspend fun makeChangeWith(times: MutableList<DateTime>) {
        times.clearTooOldTimes()

        val now = DateTime.now()

        val limitTime = now.minus(TimeSpan(SHORT_TIME_RECALCULATING_MILLIS.toDouble()))

        continuation.resumeWith(
            Result.success(
                times.asSequence().filter {
                    limitTime < it
                }.min()
            )
        )
    }
}

class TimeManager(
    scope: CoroutineScope
) : Closeable {
    private val actionsChannel = Channel<TimeManagerAction>(Channel.UNLIMITED)

    private val timeUpdateJob = scope.launch {
        val times = mutableListOf<DateTime>()
        for (action in actionsChannel) {
            action(times)
        }
    }

    suspend fun addTimeAndClear() {
        actionsChannel.send(TimeManagerTimeAdder())
    }

    suspend fun getMostOldestInLongPeriod(): DateTime? {
        return suspendCoroutine {
            actionsChannel.offer(
                TimeManagerMostOldestInLongGetter(it)
            )
        }
    }

    suspend fun getMostOldestInShortPeriod(): DateTime? {
        return suspendCoroutine {
            actionsChannel.offer(TimeManagerMostOldestInShortGetter(it))
        }
    }

    override fun close() {
        actionsChannel.close()
        timeUpdateJob.cancel()
    }
}
