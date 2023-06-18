package dev.inmo.saucenaoapi.utils

import korlibs.time.DateTime
import dev.inmo.saucenaoapi.additional.LONG_TIME_RECALCULATING_MILLIS
import dev.inmo.saucenaoapi.additional.SHORT_TIME_RECALCULATING_MILLIS
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

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
    private val deferred: CompletableDeferred<DateTime?>
) : TimeManagerAction {
    override suspend fun makeChangeWith(times: MutableList<DateTime>) {
        times.clearTooOldTimes()
        deferred.complete(times.minOrNull())
    }
}

private data class TimeManagerMostOldestInShortGetter(
    private val deferred: CompletableDeferred<DateTime?>
) : TimeManagerAction {
    override suspend fun makeChangeWith(times: MutableList<DateTime>) {
        times.clearTooOldTimes()

        val now = DateTime.now()

        val limitTime = now - SHORT_TIME_RECALCULATING_MILLIS

        deferred.complete(
            times.asSequence().filter {
                limitTime < it
            }.minOrNull()
        )
    }
}

internal class TimeManager(
    scope: CoroutineScope
) {
    private val actionsChannel = Channel<TimeManagerAction>(Channel.UNLIMITED)

    private val timeUpdateJob = scope.launch {
        val times = mutableListOf<DateTime>()
        for (action in actionsChannel) {
            action(times)
        }
    }.also {
        it.invokeOnCompletion {
            actionsChannel.close(it)
        }
    }

    suspend fun addTimeAndClear() {
        actionsChannel.send(TimeManagerTimeAdder())
    }

    suspend fun getMostOldestInLongPeriod(): DateTime? {
        val deferred = CompletableDeferred<DateTime?>()
        return if (actionsChannel.trySend(TimeManagerMostOldestInLongGetter(deferred)).isSuccess) {
            deferred.await()
        } else {
            null
        }
    }

    suspend fun getMostOldestInShortPeriod(): DateTime? {
        val deferred = CompletableDeferred<DateTime?>()
        return if (actionsChannel.trySend(TimeManagerMostOldestInShortGetter(deferred)).isSuccess) {
            deferred.await()
        } else {
            null
        }
    }
}
