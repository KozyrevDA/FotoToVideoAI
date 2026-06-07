package com.msilimon.cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap

internal expect class CroppingManager() {
    fun cropImageByImageBitmap(
        imageBitmap: ImageBitmap,
        cropPosition: Offset,
        cropSize: Size,
        windowSize: Size,
    ): ImageBitmap
}