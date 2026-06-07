package data.network.videos.dto

import data.model.Video
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    @SerialName("id_video") val idVideo: String,
    @SerialName("orientation_type") val orientationType: String,
    @SerialName("created_at") val createdAt: String,
)

fun VideoDto.toModel() = Video(
    idVideo = idVideo,
    createdAt = LocalDateTime.parse(createdAt)
)