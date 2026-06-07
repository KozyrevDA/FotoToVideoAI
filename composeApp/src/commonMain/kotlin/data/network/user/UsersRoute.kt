package data.network.user

import data.model.AuthMethod
import data.model.AuthResponse
import data.model.AuthTokens
import data.network.SpaceXApi
import data.network.user.dto.UserDto
import getPlatform
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val USERS_AUTH_VK = "users/auth-vk"
private const val USERS_AUTH_APPLE = "users/auth-apple"
private const val USERS_REFRESH = "users/refresh"
private const val USERS_AUTHENTICATE = "users/authenticate"
private const val USERS_INFO = "users/info"
private const val GENERATION_AVAILABLE = "v2/generation-available"
private const val USERS_DELETE_ACCOUNT = "users/delete-account"
private val refreshMutex = Mutex()

@Throws(Exception::class)
suspend fun SpaceXApi.authVK(
    email: String,
    fullName: String,
    accessToken: String,
    idVk: String,
): AuthResponse {
    return try {
        val response = httpClient.post(USERS_AUTH_VK) {
            parameter("email", email)
            parameter("full_name", fullName)
            parameter("access_token", accessToken)
            parameter("id_vk", idVk)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val authTokens = response.body<AuthTokens>()
                tokensManager.putAuthTokens(authTokens)
                AuthResponse.Authorized()
            }

            HttpStatusCode.Unauthorized -> tryRefresh()
            else -> AuthResponse.Error(message = response.bodyAsText())
        }
    } catch (e: Exception) {
        AuthResponse.UnknownError
    }
}

@Throws(Exception::class)
suspend fun SpaceXApi.authApple(
    email: String?,
    fullName: String?,
    accessToken: String,
    idApple: String,
): AuthResponse {
    return try {
        val response = httpClient.post(USERS_AUTH_APPLE) {
            parameter("email", email)
            parameter("full_name", fullName)
            parameter("access_token", accessToken)
            parameter("id_apple", idApple)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val authTokens = response.body<AuthTokens>()
                tokensManager.putAuthTokens(authTokens)
                AuthResponse.Authorized()
            }

            HttpStatusCode.Unauthorized -> tryRefresh()
            else -> AuthResponse.Error(message = response.bodyAsText())
        }
    } catch (e: Exception) {
        AuthResponse.UnknownError
    }
}

@Throws(Exception::class)
suspend fun SpaceXApi.signIn(
    email: String,
    password: String,
): AuthResponse {
    return try {
        val response = httpClient.post("users/signin") {
            parameter("email", email)
            parameter("pass", password)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val authTokens = response.body<AuthTokens>()
                tokensManager.putAuthTokens(authTokens)
                AuthResponse.Authorized()
            }

            else -> {
                AuthResponse.Error(message = response.bodyAsText())
            }
        }
    } catch (e: Exception) {
        AuthResponse.Error(message = e.message ?: "Unknown error")
    }
}

@Throws(Exception::class)
suspend fun SpaceXApi.authenticate(): AuthResponse {
    return try {
        val response = httpClient.get(USERS_AUTHENTICATE) {
            header(
                HttpHeaders.Authorization,
                "Bearer ${tokensManager.getAutTokens()?.accessToken}"
            )
            parameter("device_uid", deviceUid.uid.firstOrNull())
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                AuthResponse.Authorized(authMethod = AuthMethod.valueOf(response.bodyAsText()))
            }

            HttpStatusCode.Unauthorized -> {
                tryRefresh()
            }

            else -> AuthResponse.Error(message = response.bodyAsText())
        }
    } catch (e: Exception) {
        AuthResponse.UnknownError
    }
}

@Throws(Exception::class)
suspend fun SpaceXApi.getUser(): UserDto? = try {
    suspend fun SpaceXApi.doAuthRequest(): HttpResponse? = httpClient.get(USERS_INFO) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
    }

    val response = doAuthRequest() ?: return null

    when (response.status) {
        HttpStatusCode.OK -> response.body<UserDto>()
        HttpStatusCode.Unauthorized -> {
            if (tryRefresh() is AuthResponse.Authorized) {
                val retryResponse = doAuthRequest() ?: return null
                if (retryResponse.status == HttpStatusCode.OK) {
                    retryResponse.body<UserDto>()
                } else {
                    null
                }
            } else {
                null
            }
        }

        else -> null
    }
} catch (e: Exception) {
    null
}

suspend fun SpaceXApi.tryRefresh(): AuthResponse = refreshMutex.withLock {
    tokensManager.refreshTokenExist()?.let { refreshToken ->
        when (val refreshAuthResponse = refresh(refreshToken)) {
            is AuthResponse.Authorized -> {
                if (refreshAuthResponse.authTokens != null) {
                    tokensManager.putAuthTokens(refreshAuthResponse.authTokens)
                    return AuthResponse.Authorized()
                }
            }

            is AuthResponse.Unauthorized -> {
                tokensManager.logout()
            }

            else -> {}
        }
    }

    return AuthResponse.Unauthorized
}

@Throws(Exception::class)
private suspend fun SpaceXApi.refresh(refreshToken: String): AuthResponse {
    return try {
        val response = httpClient.post(USERS_REFRESH) {
            parameter("token", refreshToken)
        }

        when (response.status) {
            HttpStatusCode.OK -> AuthResponse.Authorized(response.body<AuthTokens>())
            HttpStatusCode.Conflict -> AuthResponse.Unauthorized
            else -> AuthResponse.Error(message = response.bodyAsText())
        }
    } catch (e: Exception) {
        AuthResponse.UnknownError
    }
}

@Throws(Exception::class)
fun SpaceXApi.logout() {
    tokensManager.logout()
}

@Throws(Exception::class)
suspend fun SpaceXApi.generationAvailable(): Boolean = try {
    suspend fun SpaceXApi.doAuthRequest(): HttpResponse? = httpClient.get(GENERATION_AVAILABLE) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
        parameter("platform_name", getPlatform().name.name)
    }

    val response = doAuthRequest() ?: return false

    when (response.status) {
        HttpStatusCode.OK -> response.body<Boolean>()
        HttpStatusCode.Unauthorized -> {
            if (tryRefresh() is AuthResponse.Authorized) {
                val retryResponse = doAuthRequest() ?: return false
                if (retryResponse.status == HttpStatusCode.OK) {
                    retryResponse.body<Boolean>()
                } else {
                    false
                }
            } else {
                false
            }
        }

        else -> false
    }
} catch (_: Exception) {
    false
}

@Throws(Exception::class)
suspend fun SpaceXApi.deleteAccount(): Boolean = try {
    suspend fun SpaceXApi.doAuthRequest(): HttpResponse? = httpClient.get(USERS_DELETE_ACCOUNT) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
    }

    val response = doAuthRequest() ?: return false

    when (response.status) {
        HttpStatusCode.OK -> true
        HttpStatusCode.Unauthorized -> {
            if (tryRefresh() is AuthResponse.Authorized) {
                val retryResponse = doAuthRequest() ?: return false
                retryResponse.status == HttpStatusCode.OK
            } else {
                false
            }
        }

        else -> false
    }
} catch (_: Exception) {
    false
}