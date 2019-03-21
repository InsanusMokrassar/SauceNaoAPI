package com.github.insanusmokrassar.SauceNaoAPI.utils

import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.StringSerializer


@Serializer(String::class)
object CommonMultivariantStringSerializer : KSerializer<String> by StringSerializer {
    private val stringArraySerializer = ArrayListSerializer(StringSerializer)

    override fun deserialize(decoder: Decoder): String {
        return try {
            decoder.decodeSerializableValue(StringSerializer)
        } catch (e: Exception) {
            decoder.decodeSerializableValue(stringArraySerializer).joinToString()
        }
    }
}
