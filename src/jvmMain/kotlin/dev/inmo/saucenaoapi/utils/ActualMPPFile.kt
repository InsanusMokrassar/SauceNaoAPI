package dev.inmo.saucenaoapi.utils

import io.ktor.http.ContentType
import io.ktor.utils.io.core.Input
import io.ktor.utils.io.streams.asInput
import java.io.File
import java.nio.file.Files

actual typealias MPPFile = File

actual val MPPFile.input: Input
    get() = inputStream().asInput()
actual val MPPFile.contentType: ContentType
    get() = ContentType.parse(Files.probeContentType(toPath()))
