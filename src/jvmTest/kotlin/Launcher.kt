import dev.inmo.saucenaoapi.SauceNaoAPI
import io.ktor.client.HttpClient
import kotlinx.coroutines.*
import java.io.File

suspend fun main(vararg args: String) {
    val (key, requestSubject) = args

    val client = HttpClient()
    val scope = CoroutineScope(Dispatchers.IO).also {
        it.coroutineContext.job.invokeOnCompletion {
            client.close()
        }
    }

    val api = SauceNaoAPI(key, client,  scope = scope)
    println(
        when {
            requestSubject.startsWith("/") -> File(requestSubject).let {
                api.request(it)
            }
            else -> api.request(requestSubject)
        }
    )

    scope.cancel()
}
