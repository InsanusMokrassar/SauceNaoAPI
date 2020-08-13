package com.github.insanusmokrassar.SauceNaoAPI.utils

import io.ktor.http.ContentType
import io.ktor.util.asStream
import io.ktor.utils.io.core.Input
import java.io.InputStream
import java.net.URLConnection

val InputStream.mimeType: ContentType
    get() {
        val contentType = URLConnection.guessContentTypeFromStream(this)
        return ContentType.parse(contentType)
    }

val Input.mimeType: ContentType
    get() = asStream().mimeType
