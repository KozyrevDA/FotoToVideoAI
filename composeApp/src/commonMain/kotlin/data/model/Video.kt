package data.model

import data.local.entities.VideoEntity
import kotlinx.datetime.LocalDateTime

data class Video(
    val idVideo: String,
    val createdAt: LocalDateTime,
)

fun Video.toEntity() = VideoEntity(
    id = idVideo,
    createdAt = createdAt.toString()
)