package dev.inmo.saucenaoapi.additional

import korlibs.time.TimeSpan

typealias AccountType = Int
const val defaultAccountType: AccountType = 1 // "basic"

typealias UserId = Int

val SHORT_TIME_RECALCULATING_MILLIS = TimeSpan(30.0 * 1000)
val LONG_TIME_RECALCULATING_MILLIS = TimeSpan(24.0 * 60 * 60 * 1000)
