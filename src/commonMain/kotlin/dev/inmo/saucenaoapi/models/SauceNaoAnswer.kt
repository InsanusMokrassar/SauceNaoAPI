package dev.inmo.saucenaoapi.models

import dev.inmo.saucenaoapi.defaultSauceNaoParser
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Serializable
private data class TemporalSauceNaoAnswerRepresentation(
    val header: Header,
    val results: List<Result> = emptyList(),
)

@Serializable(SauceNaoAnswerSerializer::class)
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
    private val serializer = defaultSauceNaoParser

    override fun deserialize(decoder: Decoder): SauceNaoAnswer {
        val raw = JsonObject.serializer().deserialize(decoder)

        return serializer.decodeFromJsonElement(
            TemporalSauceNaoAnswerRepresentation.serializer(),
            raw
        ).let {
            SauceNaoAnswer(
                it.header,
                it.results,
                raw
            )
        }
    }

    override fun serialize(encoder: Encoder, value: SauceNaoAnswer) {
        val resultObject = buildJsonObject {
            value.raw.forEach {
                put(it.key, it.value)
            }
            put(headerField, serializer.encodeToJsonElement(Header.serializer(), value.header))
            put(resultsField, serializer.encodeToJsonElement(resultsSerializer, value.results))
        }
        JsonObject.serializer().serialize(encoder, resultObject)
    }
}
