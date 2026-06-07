package data.network.templates.dto

sealed class GetTemplates {
    data class AllTemplates(val templates: List<TemplateDto>) : GetTemplates()
    data class OneFilter(val template: TemplateDto) : GetTemplates()
    object NoImages : GetTemplates()
    object Error : GetTemplates()
    object UnknownError : GetTemplates()
}