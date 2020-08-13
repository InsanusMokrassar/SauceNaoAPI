package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*

@Serializable
data class SauceNaoAnswer internal constructor(
    val header: Header,
    val results: List<Result> = emptyList(),
    val raw: JsonObject = JsonObject(emptyMap())
)

@Serializer(SauceNaoAnswer::class)
object SauceNaoAnswerSerializer : KSerializer<SauceNaoAnswer> {
    private val resultsSerializer = ListSerializer(Result.serializer())
    private const val headerField = "header"
    private const val resultsField = "results"
    private val serializer = Json.nonstrict

    override fun deserialize(decoder: Decoder): SauceNaoAnswer {
        val raw = JsonObjectSerializer.deserialize(decoder)
        val stringRaw = serializer.stringify(JsonObjectSerializer, raw)

        return serializer.parse(
            SauceNaoAnswer.serializer(),
            stringRaw
        ).copy(
            raw = raw
        )
    }

    override fun serialize(encoder: Encoder, obj: SauceNaoAnswer) {
        val resultObject = JsonObject(
            obj.raw.content.let {
                it + mapOf(
                    headerField to serializer.toJson(Header.serializer(), obj.header),
                    resultsField to serializer.toJson(resultsSerializer, obj.results)
                )
            }
        )
        JsonObject.serializer().serialize(encoder, resultObject)
    }
}
