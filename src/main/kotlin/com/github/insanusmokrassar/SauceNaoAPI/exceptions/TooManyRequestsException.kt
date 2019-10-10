package com.github.insanusmokrassar.SauceNaoAPI.exceptions

import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import kotlinx.io.IOException

val ClientRequestException.sauceNaoAPIException: Exception
    get() = when (response.status) {
        TooManyRequests -> TooManyRequestsException()
        else -> this
    }

class TooManyRequestsException : IOException()
