package com.insanusmokrassar.SauceNaoAPI.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.*


@Serializer(String::class)
object CommonMultivariantStringSerializer : KSerializer<String> by String.serializer() {
    private val stringArraySerializer = ListSerializer(String.serializer())

    override fun deserialize(decoder: Decoder): String {
        return when (val parsed = JsonElement.serializer().deserialize(decoder)) {
            is JsonPrimitive -> parsed.content
            is JsonArray -> parsed.joinToString { it.jsonPrimitive.content }
            else -> error("Unexpected answer object has been received: $parsed")
        }
    }
}
