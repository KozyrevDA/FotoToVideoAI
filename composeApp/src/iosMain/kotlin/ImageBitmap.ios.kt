import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toPngByteArray(): ByteArray {
    val skiaImage = Image.makeFromBitmap(this.asSkiaBitmap())
    return skiaImage.encodeToData(EncodedImageFormat.PNG)?.bytes ?: ByteArray(0)
}

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}