package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.*

@Serializable(SauceNaoAnswerSerializer::class)
data class SauceNaoAnswer(
    val header: Header,
    val results: List<Result> = emptyList(),
    val raw: JsonObject
)

@Serializer(SauceNaoAnswer::class)
object SauceNaoAnswerSerializer : KSerializer<SauceNaoAnswer> {
    private val resultsSerializer = ArrayListSerializer(Result.serializer())
    private const val headerField = "header"
    private const val resultsField = "results"
    private val serializer = Json.nonstrict

    override fun deserialize(decoder: Decoder): SauceNaoAnswer {
        val raw = JsonObjectSerializer.deserialize(decoder)
        val header = serializer.fromJson(Header.serializer(), raw.getObject(headerField))
        val results = serializer.fromJson(resultsSerializer, raw.getArray(resultsField))

        return SauceNaoAnswer(header, results, raw)
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
