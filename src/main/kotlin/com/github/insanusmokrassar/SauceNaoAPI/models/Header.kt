package com.github.insanusmokrassar.SauceNaoAPI.models

import com.github.insanusmokrassar.SauceNaoAPI.utils.JsonObjectSerializer
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.Json

@Serializable
data class Header(
    @SerialName("status")
    val status: Int,
    @SerialName("results_requested")
    val resultsRequested: Int,
    @Serializable(IndexesSerializer::class)
    @SerialName("index")
    val indexes: List<HeaderIndex?>,
    @SerialName("search_depth")
    val searchDepth: Int,
    @SerialName("minimum_similarity")
    val minSimilarity: Float,
    @SerialName("query_image_display")
    val queryImageDisplay: String, // something like "userdata/uuid.png",
    @SerialName("query_image")
    val queryImage: String, // something like "uuid.jpg"
    @SerialName("results_returned")
    val resultsCount: Int,
    @SerialName("short_remaining")
    val shortRemaining: Int? = null,
    @SerialName("long_remaining")
    val longRemaining: Int? = null,
    @SerialName("short_limit")
    val shortLimit: Int? = null,
    @SerialName("long_limit")
    val longLimit: Int? = null,
    @SerialName("account_type")
    val accountType: String? = null,
    @SerialName("user_id")
    val userId: Int? = null
)

object IndexesSerializer : KSerializer<List<HeaderIndex?>> {
    override val descriptor: SerialDescriptor = StringDescriptor

    override fun deserialize(decoder: Decoder): List<HeaderIndex?> {
        val json = decoder.decodeSerializableValue(JsonObjectSerializer)
        val parsed = json.keys.mapNotNull { it.toIntOrNull() }.sorted().mapNotNull {
            val jsonObject = json.getObjectOrNull(it.toString()) ?: return@mapNotNull null
            val index = Json.nonstrict.parse(HeaderIndex.serializer(), Json.stringify(JsonObjectSerializer, jsonObject))
            it to index
        }.toMap()
        return Array<HeaderIndex?>(parsed.keys.max() ?: 0) {
            parsed[it]
        }.toList()
    }

    override fun serialize(encoder: Encoder, obj: List<HeaderIndex?>) {
        TODO()
    }
}
