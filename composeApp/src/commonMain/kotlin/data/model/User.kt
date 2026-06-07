package data.model

import data.network.user.dto.UserDto

data class User(
    val name: String?,
    val email: String?,
    val emailReg: String,
    val coins: Int,
)

fun User.toDto() = UserDto(
    name = name,
    email = email,
    emailReg = emailReg,
    coins = coins,
)