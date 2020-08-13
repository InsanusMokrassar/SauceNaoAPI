package com.github.insanusmokrassar.SauceNaoAPI.exceptions

import com.github.insanusmokrassar.SauceNaoAPI.additional.LONG_TIME_RECALCULATING_MILLIS
import com.github.insanusmokrassar.SauceNaoAPI.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.soywiz.klock.TimeSpan
import io.ktor.client.features.ClientRequestException
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.utils.io.errors.IOException

internal suspend fun ClientRequestException.sauceNaoAPIException(): Exception  {
        return when (response.status) {
            TooManyRequests -> {
                val answerContent = response.readText()
                when {
                    answerContent.contains("daily limit") -> TooManyRequestsLongException(answerContent)
                    else -> TooManyRequestsShortException(answerContent)
                }
            }
            else -> this
        }
    }

sealed class TooManyRequestsException : IOException() {
    abstract val answerContent: String
    abstract val waitTime: TimeSpan
}

class TooManyRequestsShortException(override val answerContent: String) : TooManyRequestsException() {
    override val waitTime: TimeSpan = SHORT_TIME_RECALCULATING_MILLIS
}
class TooManyRequestsLongException(override val answerContent: String) : TooManyRequestsException() {
    override val waitTime: TimeSpan = LONG_TIME_RECALCULATING_MILLIS
}
