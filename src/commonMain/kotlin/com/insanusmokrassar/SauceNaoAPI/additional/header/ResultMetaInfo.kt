package com.insanusmokrassar.SauceNaoAPI.additional.header

import com.insanusmokrassar.SauceNaoAPI.models.Header

data class ResultMetaInfo(
    val accountInfo: AccountInfo = AccountInfo(),
    val resultsInfo: QueryResultsMetainfo = QueryResultsMetainfo()
)

val Header.adapted
    get() = ResultMetaInfo(
        accountInfo,
        queryResults
    )
