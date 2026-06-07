package data.network.videos.dto

import data.model.QueueGen
import data.model.QueueGenStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueueGenDto(
    val uid: String,
    val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("id_video") val idVideo: String? = null,
)

fun QueueGenDto.toModel() = QueueGen(
    uid = uid,
    status = QueueGenStatus.valueOf(status),
    createdAt = LocalDateTime.parse(createdAt),
    idVideo = idVideo
)