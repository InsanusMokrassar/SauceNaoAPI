package dev.inmo.saucenaoapi

import io.ktor.http.ContentType
import io.ktor.utils.io.core.Input

internal sealed class SauceRequestSubject

internal data class UrlSauceRequestSubject(val url: String) : SauceRequestSubject()

internal data class InputRequestSubject(val input: Input, val mimeType: ContentType) : SauceRequestSubject()

internal val String.asSauceRequestSubject
    get() = UrlSauceRequestSubject(this)

internal fun Input.asSauceRequestSubject(mimeType: ContentType)
    = InputRequestSubject(this, mimeType)
