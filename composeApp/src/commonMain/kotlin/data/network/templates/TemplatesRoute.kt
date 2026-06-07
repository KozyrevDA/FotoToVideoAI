package data.network.templates

import data.network.SpaceXApi
import data.network.templates.dto.GetTemplates
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

private const val TEMPLATES_ALL = "templates/all"
private const val MP4_TEMPLATES_ALL = "templates_mp4/all"

@Throws(Exception::class)
suspend fun SpaceXApi.getAllTemplates(): GetTemplates {
    return try {
        val response = httpClient.get(TEMPLATES_ALL)

        when (response.status) {
            HttpStatusCode.OK -> GetTemplates.AllTemplates(templates = response.body())
            else -> GetTemplates.Error
        }
    } catch (_: Exception) {
        GetTemplates.UnknownError
    }
}

@Throws(Exception::class)
suspend fun SpaceXApi.getAllTemplatesMP4(): GetTemplates {
    return try {
        val response = httpClient.get(MP4_TEMPLATES_ALL)

        when (response.status) {
            HttpStatusCode.OK -> GetTemplates.AllTemplates(templates = response.body())
            else -> GetTemplates.Error
        }
    } catch (_: Exception) {
        GetTemplates.UnknownError
    }
}