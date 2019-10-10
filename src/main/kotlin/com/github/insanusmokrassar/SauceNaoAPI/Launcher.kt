package com.github.insanusmokrassar.SauceNaoAPI

import kotlinx.coroutines.*

fun main(vararg args: String) {
    val (key, requestUrl) = args

    runBlocking {
        val api = SauceNaoAPI(key, scope = GlobalScope)
        api.use {
            println(
                it.request(requestUrl)
            )
        }
    }
}
