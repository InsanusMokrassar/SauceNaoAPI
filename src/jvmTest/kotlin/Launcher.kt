import com.insanusmokrassar.SauceNaoAPI.SauceNaoAPI
import com.insanusmokrassar.SauceNaoAPI.utils.useSafe
import io.ktor.http.ContentType
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.*
import java.io.File
import java.nio.file.Files

suspend fun main(vararg args: String) {
    val (key, requestSubject) = args

    val scope = CoroutineScope(Dispatchers.Default)

    val api = SauceNaoAPI(key, scope = scope)
    api.useSafe { _ ->
        println(
            when {
                requestSubject.startsWith("/") -> File(requestSubject).let {
                    api.request(
                        it.inputStream().asInput(),
                        ContentType.parse(Files.probeContentType(it.toPath()))
                    )
                }
                else -> api.request(requestSubject)
            }
        )
    }

    scope.cancel()
}
