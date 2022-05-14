package dev.inmo.saucenaoapi.utils

import io.ktor.http.ContentType
import io.ktor.utils.io.core.Input

expect class MPPFile

expect val MPPFile.input: Input
expect val MPPFile.contentType: ContentType
