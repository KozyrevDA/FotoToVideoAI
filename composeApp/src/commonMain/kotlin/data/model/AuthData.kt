package data.model

import kotlinx.serialization.Serializable

sealed class AuthResponse {
    class Authorized(
        val authTokens: AuthTokens? = null,
        val authMethod: AuthMethod? = null,
    ) : AuthResponse()

    data object Unauthorized : AuthResponse()
    class Error(val message: String? = null) : AuthResponse()
    data object UnknownError : AuthResponse()
}

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

enum class AuthMethod {
    EMAIL, GOOGLE, VK, APPLE
}