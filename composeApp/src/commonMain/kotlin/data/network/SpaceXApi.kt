package data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import utils.device.uid.DeviceUid
import utils.tokens.TokensManager

// Production: "https://fototovideoai.store:8087/"
// iOS Simulator: "http://localhost:8088/"
const val DEFAULT_IP = "http://10.0.2.2:8088/"

class SpaceXApi(
    val tokensManager: TokensManager,
    val deviceUid: DeviceUid,
) {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 600_000
            connectTimeoutMillis = 600_000
            socketTimeoutMillis = 600_000
        }
        defaultRequest {
            url(DEFAULT_IP)
        }
    }
}