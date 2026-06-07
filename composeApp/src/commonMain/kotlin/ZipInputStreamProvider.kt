import androidx.compose.ui.graphics.ImageBitmap

expect fun unzipToBitmaps(bytes: ByteArray): Map<String, ImageBitmap>