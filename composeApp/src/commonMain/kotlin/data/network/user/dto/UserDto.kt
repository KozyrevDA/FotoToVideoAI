package data.network.user.dto

import data.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String?,
    val email: String?,
    @SerialName("email_reg") val emailReg: String,
    val coins: Int,
)

fun UserDto.toModel() = User(
    name = name,
    email = email,
    emailReg = emailReg,
    coins = coins,
)