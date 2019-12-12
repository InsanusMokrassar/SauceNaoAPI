package com.github.insanusmokrassar.SauceNaoAPI

import com.github.insanusmokrassar.SauceNaoAPI.utils.mimeType
import io.ktor.http.ContentType
import kotlinx.coroutines.*
import kotlinx.io.core.readText
import kotlinx.io.streams.asInput
import java.io.File
import java.net.URLConnection
import java.nio.file.Files

suspend fun main(vararg args: String) {
    val (key, requestSubject) = args

    val scope = CoroutineScope(Dispatchers.Default)

    val api = SauceNaoAPI(key, scope = scope)
    api.use { _ ->
        println(
            when {
                requestSubject.startsWith("/") -> File(requestSubject).let {
                    api.request(
                        it.inputStream().asInput(),
                        ContentType.parse(Files.probeContentType(it.toPath()))
                    )
                }
                else -> api.request(requestSubject)
            }
        )
    }

    scope.cancel()
}
