package data.network.videos.dto

import Language
import data.model.ChatMessage
import getLanguage
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val userPrompt: String?,
    val systemPromt: SystemPromtDto,
    val timestamp: Instant,
    val sender: SenderType,
    val language: String? = null,
)

@Serializable
enum class SenderType {
    USER,
    SERVER
}

fun ChatMessageDto.toModel() = ChatMessage(
    userPrompt = userPrompt,
    systemPromt = systemPromt.toModel(),
    isUser = when (sender) {
        SenderType.USER -> true
        SenderType.SERVER -> false
    },
    timestamp = timestamp,
    language = language?.let { Language.valueOf(it) } ?: getLanguage()
)