package dev.inmo.saucenaoapi.utils

import dev.inmo.saucenaoapi.additional.LONG_TIME_RECALCULATING_MILLIS
import dev.inmo.saucenaoapi.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.soywiz.klock.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

private fun MutableList<DateTime>.clearTooOldTimes(relatedTo: DateTime = DateTime.now()) {
    val limitValue = relatedTo - LONG_TIME_RECALCULATING_MILLIS

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
        continuation.resumeWith(Result.success(times.minOrNull()))
    }
}

private data class TimeManagerMostOldestInShortGetter(
    private val continuation: Continuation<DateTime?>
) : TimeManagerAction {
    override suspend fun makeChangeWith(times: MutableList<DateTime>) {
        times.clearTooOldTimes()

        val now = DateTime.now()

        val limitTime = now - SHORT_TIME_RECALCULATING_MILLIS

        continuation.resumeWith(
            Result.success(
                times.asSequence().filter {
                    limitTime < it
                }.minOrNull()
            )
        )
    }
}

internal class TimeManager(
    scope: CoroutineScope
) : SauceCloseable {
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
            actionsChannel.trySend(
                TimeManagerMostOldestInLongGetter(it)
            )
        }
    }

    suspend fun getMostOldestInShortPeriod(): DateTime? {
        return suspendCoroutine {
            actionsChannel.trySend(TimeManagerMostOldestInShortGetter(it))
        }
    }

    override fun close() {
        actionsChannel.close()
        timeUpdateJob.cancel()
    }
}
