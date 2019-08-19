package com.github.insanusmokrassar.SauceNaoAPI

import kotlinx.coroutines.*

fun main(vararg args: String) {
    val key = args.first()
    val api = SauceNaoAPI(key)

    val launch = GlobalScope.launch {
        api.use {
            it.request(
                args[1]
            ).also {
                println(it)
            }
        }
    }

    runBlocking {
        launch.join()
    }
}
