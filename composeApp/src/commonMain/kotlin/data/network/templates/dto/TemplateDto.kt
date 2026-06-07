package data.network.templates.dto

import data.model.Template
import kotlinx.serialization.Serializable

@Serializable
data class TemplateDto(
    val nameRu: String,
    val groupRu: String,
    val nameEn: String,
    val groupEn: String,
    val namePt: String,
    val groupPt: String,
    val path: String,
)

fun TemplateDto.toModel() = Template(
    nameRu = nameRu,
    groupRu = groupRu,
    nameEn = nameEn,
    groupEn = groupEn,
    namePt = namePt,
    groupPt = groupPt,
    path = path
)