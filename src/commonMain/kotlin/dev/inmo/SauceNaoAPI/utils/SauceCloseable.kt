package dev.inmo.SauceNaoAPI.utils

import kotlinx.coroutines.supervisorScope

interface SauceCloseable {
    fun close()
}

fun <T> SauceCloseable.use(block: (SauceCloseable) -> T): T = try {
    block(this)
} finally {
    close()
}

suspend fun <T> SauceCloseable.useSafe(block: suspend (SauceCloseable) -> T): T = try {
    supervisorScope {
        block(this@useSafe)
    }
} finally {
    close()
}
