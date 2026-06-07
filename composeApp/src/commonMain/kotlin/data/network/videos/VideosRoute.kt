package data.network.videos

import androidx.compose.ui.graphics.ImageBitmap
import data.model.AuthResponse
import data.model.QueueGenStatus
import data.network.SpaceXApi
import data.network.videos.dto.ChatMessageDto
import data.network.videos.dto.GenerateVideoResponse
import data.network.videos.dto.VideoDto
import data.network.user.tryRefresh
import data.network.videos.dto.QueueGenDto
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.toByteArray
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import toPngByteArray
import kotlin.io.encoding.ExperimentalEncodingApi

private const val POST_GENERATE_VIDEO = "/generate/video"
private const val GET_ALL_INFO_VIDEOS = "/videos/all"
private const val GET_VIDEO_FILE = "/videos/get"
private const val IMAGE_ORIGIN_UPLOAD = "/image/origin/upload"
private const val GENERATE_IS_PROCESSING = "/generate/is-processing"
private const val DELETE_VIDEO = "/videos/delete"
private const val GENERATE_STATUS_VIDEO = "/generate/status/video"
private const val GENERATE_QUEUE = "/generate/queue"

@Throws(Exception::class)
suspend fun SpaceXApi.generateVideo(
    chatMessageDto: ChatMessageDto,
    photo1: ImageBitmap?,
    photo2: ImageBitmap?,
): GenerateVideoResponse {
    suspend fun doRequest(): HttpResponse = httpClient.post(POST_GENERATE_VIDEO) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
        setBody(
            MultiPartFormDataContent(
                formData {
                    append(
                        key = "metadata",
                        value = Json.encodeToString(chatMessageDto),
                        headers = Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"metadata\""
                            )
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    )

                    photo1?.toPngByteArray()?.let { pngBytes ->
                        append(
                            key = "photo1",
                            value = pngBytes,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"photo1\"; filename=\"photo1-${Clock.System.now()}.png\""
                                )
                                append(HttpHeaders.ContentType, "image/png")
                            }
                        )
                    }

                    photo2?.toPngByteArray()?.let { pngBytes ->
                        append(
                            key = "photo2",
                            value = pngBytes,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"photo2\"; filename=\"photo2-${Clock.System.now()}.png\""
                                )
                                append(HttpHeaders.ContentType, "image/png")
                            }
                        )
                    }
                }
            )
        )
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> GenerateVideoResponse.Success()
            HttpStatusCode.PaymentRequired -> GenerateVideoResponse.NotEnoughCoins
            HttpStatusCode.Unauthorized -> GenerateVideoResponse.Error
            HttpStatusCode.InternalServerError -> GenerateVideoResponse.InternalServerError(
                response.bodyAsText()
            )

            else -> GenerateVideoResponse.Error
        }
    } catch (e: ResponseException) {
        val response = e.response
        if (response.status == HttpStatusCode.InternalServerError) {
            GenerateVideoResponse.InternalServerError(response.bodyAsText())
        } else {
            GenerateVideoResponse.Error
        }
    } catch (_: HttpRequestTimeoutException) {
        GenerateVideoResponse.InternalServerError("video generation timeout")
    } catch (_: Exception) {
        GenerateVideoResponse.UnknownError
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Throws(Exception::class)
suspend fun SpaceXApi.generationIsProcessing(): Boolean? {
    suspend fun doRequest(): HttpResponse = httpClient.get(GENERATE_IS_PROCESSING) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}


@Throws(Exception::class)
suspend fun SpaceXApi.uploadOriginImageAndPrompt(
    photo1: ImageBitmap? = null,
    photo2: ImageBitmap? = null,
    chatMessageDto: ChatMessageDto,
): Boolean {
    suspend fun doRequest(): HttpResponse = httpClient.post(IMAGE_ORIGIN_UPLOAD) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
        setBody(
            MultiPartFormDataContent(
                formData {
                    photo1?.let { img ->
                        append(
                            "photo1",
                            buildPacket { writeFully(img.toPngByteArray()) },
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"photo1-${Clock.System.now()}.png\""
                                )
                            }
                        )
                    }

                    photo2?.let { img ->
                        append(
                            "photo2",
                            buildPacket { writeFully(img.toPngByteArray()) },
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"photo2-${Clock.System.now()}.png\""
                                )
                            }
                        )
                    }

                    append(
                        "metadata",
                        Json.encodeToString(chatMessageDto),
                        Headers.build {
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    )
                }
            )
        )
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> false
        }
    } catch (_: Exception) {
        false
    }
}

suspend fun SpaceXApi.getAllInfoVideos(): List<VideoDto> {
    suspend fun doRequest(): HttpResponse = httpClient.get(GET_ALL_INFO_VIDEOS) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> response.body<List<VideoDto>>()
            else -> emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }
}

suspend fun SpaceXApi.downloadVideo(idVideo: String): GenerateVideoResponse {
    suspend fun doRequest(): HttpResponse = httpClient.get(GET_VIDEO_FILE.plus("/$idVideo")) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
        parameter("id_video", idVideo)
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val bytes = response.bodyAsChannel().toByteArray()
                GenerateVideoResponse.Success(video = bytes)
            }

            HttpStatusCode.Unauthorized -> GenerateVideoResponse.Error
            HttpStatusCode.InternalServerError -> GenerateVideoResponse.InternalServerError(
                response.bodyAsText()
            )

            else -> GenerateVideoResponse.Error
        }
    } catch (_: Exception) {
        GenerateVideoResponse.UnknownError
    }
}

suspend fun SpaceXApi.deleteVideo(idVideo: String): GenerateVideoResponse {
    suspend fun doRequest(): HttpResponse = httpClient.get(DELETE_VIDEO) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
        parameter("id_video", idVideo)
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val bytes = response.bodyAsChannel().toByteArray()
                GenerateVideoResponse.Success(video = bytes)
            }

            HttpStatusCode.Unauthorized -> GenerateVideoResponse.Error
            HttpStatusCode.InternalServerError -> GenerateVideoResponse.InternalServerError(
                response.bodyAsText()
            )

            else -> GenerateVideoResponse.Error
        }
    } catch (_: Exception) {
        GenerateVideoResponse.UnknownError
    }
}

suspend fun SpaceXApi.getStatusVideo(uid: String): QueueGenDto? {
    suspend fun doRequest(): HttpResponse = httpClient.get(GENERATE_STATUS_VIDEO) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
        parameter("uid", uid)
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

suspend fun SpaceXApi.getGenerateQueue(status: QueueGenStatus): List<QueueGenDto> {
    suspend fun doRequest(): HttpResponse = httpClient.get(GENERATE_QUEUE) {
        header(HttpHeaders.Authorization, "Bearer ${tokensManager.getAutTokens()?.accessToken}")
        parameter("status", status.name)
    }

    return try {
        var response = doRequest()

        if (response.status == HttpStatusCode.Unauthorized && tryRefresh() is AuthResponse.Authorized) {
            response = doRequest()
        }

        when (response.status) {
            HttpStatusCode.OK -> response.body()
            else -> emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }
}

private const val POST_GENERATE_TRIAL = "/generate/trial"

suspend fun SpaceXApi.generateTrialVideo(
    photo1: androidx.compose.ui.graphics.ImageBitmap,
): GenerateVideoResponse {
    return try {
        val response = httpClient.post(POST_GENERATE_TRIAL) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "device_uid",
                            value = deviceUid.uid.value ?: "",
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"device_uid\""
                                )
                            }
                        )

                        photo1.toPngByteArray().let { pngBytes ->
                            append(
                                key = "photo1",
                                value = pngBytes,
                                headers = Headers.build {
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "form-data; name=\"photo1\"; filename=\"trial-photo.png\""
                                    )
                                    append(HttpHeaders.ContentType, "image/png")
                                }
                            )
                        }
                    }
                )
            )
        }

        when (response.status) {
            HttpStatusCode.OK -> GenerateVideoResponse.Success()
            HttpStatusCode.Conflict -> GenerateVideoResponse.Error
            HttpStatusCode.InternalServerError -> GenerateVideoResponse.InternalServerError(
                response.bodyAsText()
            )
            else -> GenerateVideoResponse.Error
        }
    } catch (_: HttpRequestTimeoutException) {
        GenerateVideoResponse.InternalServerError("trial generation timeout")
    } catch (_: Exception) {
        GenerateVideoResponse.UnknownError
    }
}