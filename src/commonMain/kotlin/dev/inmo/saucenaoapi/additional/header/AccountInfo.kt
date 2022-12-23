package dev.inmo.saucenaoapi.additional.header

import dev.inmo.saucenaoapi.additional.*
import dev.inmo.saucenaoapi.models.Header

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
) : Comparable<LimitStatus> {
    override fun compareTo(other: LimitStatus): Int = when {
        limit == other.limit && remain == other.remain -> 0
        else -> remain.compareTo(other.remain)
    }
}

data class Limits(
    val short: LimitStatus = LimitStatus(),
    val long: LimitStatus = LimitStatus()
) : Comparable<Limits> {
    override fun compareTo(other: Limits): Int = when {
        long == other.long && short == other.short -> 0
        else -> short.remain.compareTo(other.short.remain)
    }
}

data class AccountInfo(
    val accountType: AccountType = defaultAccountType,
    val userId: UserId? = null,
    val limits: Limits = Limits()
) : Comparable<AccountInfo> {
    override fun compareTo(other: AccountInfo): Int = limits.compareTo(other.limits)
}
