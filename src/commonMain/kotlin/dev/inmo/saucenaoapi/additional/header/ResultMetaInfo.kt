package dev.inmo.saucenaoapi.additional.header

import dev.inmo.saucenaoapi.models.Header

data class ResultMetaInfo(
    val accountInfo: AccountInfo = AccountInfo(),
    val resultsInfo: QueryResultsMetainfo = QueryResultsMetainfo()
)

val Header.adapted
    get() = ResultMetaInfo(
        accountInfo,
        queryResults
    )
