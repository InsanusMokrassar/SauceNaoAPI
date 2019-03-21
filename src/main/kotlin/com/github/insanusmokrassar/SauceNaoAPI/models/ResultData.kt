package com.github.insanusmokrassar.SauceNaoAPI.models

import com.github.insanusmokrassar.SauceNaoAPI.utils.CommonMultivariantStringSerializer
import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.StringSerializer

@Serializable
data class ResultData(
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("danbooru_id")
    @Optional
    val danbooruId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("gelbooru_id")
    @Optional
    val gelbooruId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("drawr_id")
    @Optional
    val drawrId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("pixiv_id")
    @Optional
    val pixivId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @Optional
    val title: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @Optional
    val creator: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @Optional
    val material: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("member_name")
    @Optional
    val memberName: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("member_id")
    @Optional
    val memberId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @Optional
    val characters: String? = null,
    @SerialName("ext_urls")
    @Optional
    val extUrls: List<String> = emptyList()
)
