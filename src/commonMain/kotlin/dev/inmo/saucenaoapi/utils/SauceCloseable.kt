package dev.inmo.saucenaoapi.utils

import kotlinx.coroutines.supervisorScope

interface SauceCloseable {
    fun close()
}

inline fun <T> SauceCloseable.use(block: (SauceCloseable) -> T): T = try {
    block(this)
} finally {
    close()
}

@Deprecated("Useless")
suspend fun <T> SauceCloseable.useSafe(block: suspend (SauceCloseable) -> T): T = try {
    supervisorScope {
        block(this@useSafe)
    }
} finally {
    close()
}
