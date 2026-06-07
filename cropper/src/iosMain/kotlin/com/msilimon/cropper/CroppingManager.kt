package com.msilimon.cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import org.jetbrains.skia.Image
import platform.CoreGraphics.CGImageCreateWithImageInRect
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

internal actual class CroppingManager actual constructor() {
    @OptIn(ExperimentalForeignApi::class)
    actual fun cropImageByImageBitmap(
        imageBitmap: ImageBitmap,
        cropPosition: Offset,
        cropSize: Size,
        windowSize: Size,
    ): ImageBitmap {
        try {
            val image = Image.makeFromBitmap(imageBitmap.asSkiaBitmap())
            val byteArray = image.encodeToData()?.bytes ?: return ImageBitmap(0, 0)
            val nsData = byteArray.toNSData()
            val uiImage = UIImage.imageWithData(nsData) ?: return ImageBitmap(0, 0)
            val cgImage = uiImage.CGImage ?: return ImageBitmap(0, 0)
            val cropRect = CGRectMake(
                x = cropPosition.x.toDouble(),
                y = cropPosition.y.toDouble(),
                width = cropSize.width.toDouble(),
                height = cropSize.height.toDouble(),
            )
            val croppedCGImage = CGImageCreateWithImageInRect(cgImage, cropRect)
                ?: return ImageBitmap(0, 0)
            val croppedUIImage = UIImage.imageWithCGImage(croppedCGImage)

            return Image.makeFromEncoded(croppedUIImage.toByteArray()).toComposeImageBitmap()
        } catch (e: Exception) {
            return ImageBitmap(0, 0)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toByteArray(): ByteArray {
    val imageData = UIImageJPEGRepresentation(this, 0.99)
        ?: throw IllegalArgumentException("image data is null")
    val bytes = imageData.bytes ?: throw IllegalArgumentException("image bytes is null")
    val length = imageData.length

    val data: CPointer<ByteVar> = bytes.reinterpret()
    return ByteArray(length.toInt()) { index -> data[index] }
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData = memScoped {
    NSData.create(bytes = allocArrayOf(this@toNSData), length = this@toNSData.size.convert())
}