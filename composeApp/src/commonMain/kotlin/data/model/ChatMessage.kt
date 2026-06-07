package data.model

import Language
import data.network.videos.dto.ChatMessageDto
import data.network.videos.dto.SenderType
import kotlinx.datetime.Instant

data class ChatMessage(
    val userPrompt: String?,
    val systemPromt: SystemPromt,
    val isUser: Boolean,
    val timestamp: Instant,
    val language: Language,
)

fun ChatMessage.toDto() = ChatMessageDto(
    userPrompt = userPrompt,
    systemPromt = systemPromt.toDto(),
    sender = if (isUser) SenderType.USER else SenderType.SERVER,
    timestamp = timestamp,
    language = language.name
)