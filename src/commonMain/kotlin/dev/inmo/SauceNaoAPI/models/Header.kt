package dev.inmo.SauceNaoAPI.models

import com.insanusmokrassar.SauceNaoAPI.defaultSauceNaoParser
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Serializable
data class Header(
    @SerialName("status")
    val status: Int? = null,
    @SerialName("results_requested")
    val resultsRequested: Int? = null,
    @Serializable(IndexesSerializer::class)
    @SerialName("index")
    val indexes: List<HeaderIndex?> = emptyList(),
    @SerialName("search_depth")
    val searchDepth: Int? = null,
    @SerialName("minimum_similarity")
    val minSimilarity: Float? = null,
    @SerialName("results_returned")
    val resultsCount: Int? = null,
    @SerialName("query_image_display")
    val queryImageDisplay: String? = null, // something like "userdata/uuid.png",
    @SerialName("query_image")
    val queryImage: String? = null, // something like "uuid.jpg"
    @SerialName("short_remaining")
    val shortRemaining: Int = Int.MAX_VALUE,
    @SerialName("long_remaining")
    val longRemaining: Int = Int.MAX_VALUE,
    @SerialName("short_limit")
    val shortLimit: Int = Int.MAX_VALUE,
    @SerialName("long_limit")
    val longLimit: Int = Int.MAX_VALUE,
    @SerialName("account_type")
    val accountType: Int? = null,
    @SerialName("user_id")
    val userId: Int? = null
)

internal object IndexesSerializer : KSerializer<List<HeaderIndex?>> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<HeaderIndex?> {
        val json = JsonObject.serializer().deserialize(decoder)
        val parsed = json.keys.mapNotNull { it.toIntOrNull() }.sorted().mapNotNull {
            val jsonObject = json[it.toString()] ?.jsonObject ?: return@mapNotNull null
            val index = defaultSauceNaoParser.decodeFromString(
                HeaderIndex.serializer(),
                defaultSauceNaoParser.encodeToString(JsonObject.serializer(), jsonObject)
            )
            it to index
        }.toMap()
        return Array<HeaderIndex?>(parsed.keys.maxOrNull() ?: 0) {
            parsed[it]
        }.toList()
    }

    override fun serialize(encoder: Encoder, value: List<HeaderIndex?>) {
        TODO()
    }
}
