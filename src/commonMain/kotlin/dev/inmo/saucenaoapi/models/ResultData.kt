package dev.inmo.saucenaoapi.models

import dev.inmo.saucenaoapi.utils.CommonMultivariantStringSerializer
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
    @SerialName("seiga_id")
    val seigaId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("yandere_id")
    val yandereId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("konachan_id")
    val konachanId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("sankaku_id")
    val sankakuId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("anime-pictures_id")
    val animePicturesId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("e621_id")
    val e621Id: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("idol_id")
    val idolId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("anidb_aid")
    val anidbAId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("bcy_id")
    val bcyId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("ddb_id")
    val ddbId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("nijie_id")
    val nijieId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("getchu_id")
    val getchuId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("shutterstock_id")
    val shutterstockId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("contributor_id")
    val contributorId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("est_time")
    val estTime: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("bcy_type")
    val bcyType: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("da_id")
    val daId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("pg_id")
    val pgId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("mal_id")
    val malId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("md_id")
    val mdId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("mu_id")
    val muId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("pawoo_id")
    val pawooId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("pawoo_user_acct")
    val pawooUserAcct: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("pawoo_user_username")
    val pawooUserUsername: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("pawoo_user_display_name")
    val pawooUserDisplayname: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val title: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("jp_title")
    val titleJp: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("eng_title")
    val titleEng: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("alt_titles")
    val titleAlt: List<String> = emptyList(),
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("jp_name")
    val nameJp: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("eng_name")
    val nameEng: String? = null,
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
    val part: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("part_name")
    val partName: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val date: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val company: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val file: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val year: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("member_link_id")
    val memberLinkId: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("author_name")
    val authorName: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("author_url")
    val authorUrl: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val characters: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val source: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val url: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    val type: String? = null,
    @Serializable(CommonMultivariantStringSerializer::class)
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("ext_urls")
    val extUrls: List<String> = emptyList()
)
