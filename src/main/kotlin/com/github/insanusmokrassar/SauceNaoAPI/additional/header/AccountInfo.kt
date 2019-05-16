package com.github.insanusmokrassar.SauceNaoAPI.additional.header

import com.github.insanusmokrassar.SauceNaoAPI.additional.*
import com.github.insanusmokrassar.SauceNaoAPI.models.Header

val Header.shortLimitStatus: LimitStatus
    get() = LimitStatus(
        shortRemaining ?: Int.MAX_VALUE,
        shortLimit ?: Int.MAX_VALUE
    )

val Header.longLimitStatus: LimitStatus
    get() = LimitStatus(
        longRemaining ?: Int.MAX_VALUE,
        longLimit ?: Int.MAX_VALUE
    )

val Header.limits
    get() = Limits(shortLimitStatus, longLimitStatus)

val Header.accountInfo
    get() = AccountInfo(
        accountType ?: defaultAccountType,
        userId,
        limits
    )


data class LimitStatus(
    val remain: Int = Int.MAX_VALUE,
    val limit: Int = Int.MAX_VALUE
)

data class Limits(
    val short: LimitStatus = LimitStatus(),
    val long: LimitStatus = LimitStatus()
)

data class AccountInfo(
    val accountType: AccountType = defaultAccountType,
    val userId: UserId? = null,
    val limits: Limits = Limits()
)
