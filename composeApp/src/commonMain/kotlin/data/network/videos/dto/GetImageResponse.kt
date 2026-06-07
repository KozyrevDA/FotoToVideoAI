package data.network.videos.dto

import androidx.compose.ui.graphics.ImageBitmap

sealed class GetImageResponse {
    data class Success(val images: Map<String, ImageBitmap>) : GetImageResponse()
    object NoImages : GetImageResponse()
    object Error : GetImageResponse()
    object UnknownError : GetImageResponse()
}