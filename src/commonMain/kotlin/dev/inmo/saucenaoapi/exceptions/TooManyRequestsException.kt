package dev.inmo.saucenaoapi.exceptions

import dev.inmo.saucenaoapi.additional.LONG_TIME_RECALCULATING_MILLIS
import dev.inmo.saucenaoapi.additional.SHORT_TIME_RECALCULATING_MILLIS
import com.soywiz.klock.TimeSpan
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.utils.io.errors.IOException

internal suspend fun ClientRequestException.sauceNaoAPIException(): Exception  {
    return when (response.status) {
        TooManyRequests -> {
            val answerContent = response.bodyAsText()
            when {
                answerContent.contains("daily limit") -> TooManyRequestsLongException(answerContent)
                else -> TooManyRequestsShortException(answerContent)
            }
        }
        else -> this
    }
}

sealed class TooManyRequestsException(message: String, cause: Throwable? = null) : IOException(message, cause) {
    abstract val answerContent: String
    abstract val waitTime: TimeSpan
}

class TooManyRequestsShortException(override val answerContent: String) : TooManyRequestsException("Too many requests were sent in the short period") {
    override val waitTime: TimeSpan = SHORT_TIME_RECALCULATING_MILLIS
}
class TooManyRequestsLongException(override val answerContent: String) : TooManyRequestsException("Too many requests were sent in the long period") {
    override val waitTime: TimeSpan = LONG_TIME_RECALCULATING_MILLIS
}
