package data.model

import kotlinx.datetime.LocalDateTime

data class QueueGen(
    val uid: String,
    val status: QueueGenStatus,
    val createdAt: LocalDateTime,
    val idVideo: String? = null,
)

enum class QueueGenStatus {
    CREATED,
    GENERATION,
    COMPLETED,
    FAILED,
    ERROR_SAFETY_SYSTEM_HUMAN,
    ERROR_SAFETY_SYSTEM_SEX_CONTENT,
    TERMS_OF_SERVICE,
    TIMEOUT,
    MINOR_CHILDREN
}