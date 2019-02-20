package com.github.insanusmokrassar.SauceNaoAPI

import kotlinx.coroutines.runBlocking

fun main(vararg args: String) {
    val key = args.first()
    val api = SauceNaoAPI(key)

    runBlocking {
        api.request(
            args[1]
        ).also {
            println(it)
        }
    }
}
