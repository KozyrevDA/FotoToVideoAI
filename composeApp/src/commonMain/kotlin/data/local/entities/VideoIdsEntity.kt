package data.local.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoEntity(
    val id: String,
    @SerialName("created_at") val createdAt: String,
)