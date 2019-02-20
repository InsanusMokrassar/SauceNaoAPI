package com.github.insanusmokrassar.SauceNaoAPI.models

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultItem(
    @SerialName("danbooru_id")
    @Optional
    val danbooruId: String? = null,
    @SerialName("gelbooru_id")
    @Optional
    val gelbooruId: String? = null,
    @SerialName("drawr_id")
    @Optional
    val drawrId: String? = null,
    @Optional
    val creator: String? = null,
    @Optional
    val material: String? = null,
    @SerialName("member_name")
    @Optional
    val memberName: String? = null,
    @SerialName("member_id")
    @Optional
    val memberId: String? = null,
    @Optional
    val characters: String? = null,
    @SerialName("ext_urls")
    @Optional
    val extUrls: List<String> = emptyList()
)