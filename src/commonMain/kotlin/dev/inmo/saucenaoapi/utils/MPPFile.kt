package dev.inmo.saucenaoapi.utils

import dev.inmo.micro_utils.common.MPPFile
import dev.inmo.micro_utils.common.filename
import dev.inmo.micro_utils.ktor.common.input
import dev.inmo.micro_utils.mime_types.KnownMimeTypes
import dev.inmo.micro_utils.mime_types.getMimeType
import io.ktor.http.ContentType
import io.ktor.utils.io.core.Input

@Deprecated(
    "MPPFile from microutils is preferable since 0.16.0",
    ReplaceWith("MPPFile", "dev.inmo.micro_utils.common.MPPFile")
)
typealias MPPFile = MPPFile

@Deprecated(
    "input() from microutils is preferable since 0.16.0",
    ReplaceWith("this.input()", "dev.inmo.micro_utils.ktor.common.input")
)
val MPPFile.input: Input
    get() = input()
val MPPFile.contentType: ContentType
    get() = ContentType.parse(
        getMimeType(stringWithExtension = filename.extension) ?.raw ?: KnownMimeTypes.Any.raw
    )
