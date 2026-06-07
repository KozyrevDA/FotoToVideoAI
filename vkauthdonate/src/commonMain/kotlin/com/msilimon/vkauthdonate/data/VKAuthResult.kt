package com.msilimon.vkauthdonate.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class VKAuthResult {
    data class LoggedIn(
        @SerialName("id_vk") val idVk: String?,
    ) : VKAuthResult()

    @Serializable
    data class Authorized(
        @SerialName("email") val email: String?,
        @SerialName("full_name") val fullName: String?,
        @SerialName("access_token") val accessToken: String?,
        @SerialName("id_vk") val idVk: String?,
    ) : VKAuthResult()

    data object Unauthorized : VKAuthResult()

    data object Logout : VKAuthResult()
}