package dev.inmo.SauceNaoAPI.additional.header

import com.insanusmokrassar.SauceNaoAPI.additional.*
import com.insanusmokrassar.SauceNaoAPI.models.Header

val Header.shortLimitStatus: LimitStatus
    get() = LimitStatus(
        shortRemaining,
        shortLimit
    )

val Header.longLimitStatus: LimitStatus
    get() = LimitStatus(
        longRemaining,
        longLimit
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
