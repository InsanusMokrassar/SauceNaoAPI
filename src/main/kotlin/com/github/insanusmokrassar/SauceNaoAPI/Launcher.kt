package com.github.insanusmokrassar.SauceNaoAPI

import kotlinx.coroutines.*

suspend fun main(vararg args: String) {
    val (key, requestUrl) = args

    val api = SauceNaoAPI(key, scope = GlobalScope)
    api.use {
        println(
            it.request(requestUrl)
        )
    }
}
