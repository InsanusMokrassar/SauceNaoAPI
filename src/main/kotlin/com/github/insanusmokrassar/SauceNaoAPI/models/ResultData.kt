package com.github.insanusmokrassar.SauceNaoAPI.models

import com.github.insanusmokrassar.SauceNaoAPI.utils.CommonMultivariantStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultData(
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("danbooru_id")
    val danbooruId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("gelbooru_id")
    val gelbooruId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("drawr_id")
    val drawrId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("pixiv_id")
    val pixivId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val title: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val creator: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val material: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("member_name")
    val memberName: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("member_id")
    val memberId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val characters: String? = null,
    @SerialName("ext_urls")
    val extUrls: List<String> = emptyList()
)
