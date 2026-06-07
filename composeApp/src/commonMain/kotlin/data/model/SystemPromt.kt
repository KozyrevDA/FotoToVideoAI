package data.model

import data.network.videos.dto.SystemPromtDto

data class SystemPromt(
    val nameFilter: String,
    val secondTemplatePath: String?,
)

fun SystemPromt.toDto() = SystemPromtDto(
    nameFilter = nameFilter,
    secondTemplatePath = secondTemplatePath
)