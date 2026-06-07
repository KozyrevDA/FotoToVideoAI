package utils.extensions

import data.network.DEFAULT_IP
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

suspend inline fun <reified T> HttpClient.getStatusOK(
    path: String,
    action: (value: T?) -> Unit,
) {
    try {
        val response = get(path)

        when (response.status) {
            HttpStatusCode.OK -> {
                action(response.body())
            }

            HttpStatusCode.BadRequest -> {
                action(null)
            }
        }
    } catch (e: Throwable) {
        action(null)
    }
}

suspend inline fun <reified T> HttpClient.getStatusOK(
    path: String,
    params: Map<String, String> = mapOf(),
    action: (value: T?) -> Unit,
) {
    try {
        val response = get(path) {
            params.forEach {
                parameter(it.key, it.value)
            }
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                action(response.body())
            }

            HttpStatusCode.BadRequest -> {
                action(null)
            }
        }
    } catch (e: Throwable) {
        action(null)
    }
}

fun drawableRemote(name: String): String {
    return DEFAULT_IP.plus("templates/$name")
}