import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun unzipToBitmaps(bytes: ByteArray): Map<String, ImageBitmap> = emptyMap()