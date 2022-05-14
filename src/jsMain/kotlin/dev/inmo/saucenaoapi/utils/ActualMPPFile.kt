package dev.inmo.saucenaoapi.utils

import io.ktor.http.ContentType
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.Input
import org.khronos.webgl.Int8Array
import org.w3c.files.File
import org.w3c.files.FileReaderSync

actual typealias MPPFile = File

actual val MPPFile.input: Input
    get() {
        val reader = FileReaderSync()
        return ByteReadPacket(Int8Array(reader.readAsArrayBuffer(this)) as ByteArray)
    }
actual val MPPFile.contentType: ContentType
    get() = ContentType.parse(type)
