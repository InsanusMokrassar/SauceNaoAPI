package dev.inmo.SauceNaoAPI.additional.header

import com.insanusmokrassar.SauceNaoAPI.models.Header

data class IndexInfo(
    val id: Int,
    val status: Int = 500,
    val results: Int = 0,
    val parent_id: Int? = null
)

val Header.adaptedIndexes: List<IndexInfo>
    get() = indexes.mapNotNull {
        it ?.let { _ ->
            it.id ?.let { id ->
                IndexInfo(
                    id,
                    it.status ?: 500, // Serverside error if not set
                    it.results ?: 0,
                    it.parent_id
                )
            }
        }
    }
