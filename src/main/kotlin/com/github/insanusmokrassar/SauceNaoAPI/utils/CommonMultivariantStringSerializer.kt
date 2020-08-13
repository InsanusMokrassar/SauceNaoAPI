package com.github.insanusmokrassar.SauceNaoAPI.utils

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer


@Serializer(String::class)
object CommonMultivariantStringSerializer : KSerializer<String> by String.serializer() {
    private val stringArraySerializer = ListSerializer(String.serializer())

    override fun deserialize(decoder: Decoder): String {
        return try {
            decoder.decodeSerializableValue(String.serializer())
        } catch (e: Exception) {
            decoder.decodeSerializableValue(stringArraySerializer).joinToString()
        }
    }
}
