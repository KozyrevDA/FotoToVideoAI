import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.zip.ZipInputStream

actual fun unzipToBitmaps(bytes: ByteArray): Map<String, ImageBitmap> {
    val bitmaps = mutableMapOf<String, ImageBitmap>()
    ZipInputStream(bytes.inputStream()).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                val name = entry.name
                val fileBytes = zip.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.size)
                    ?.asImageBitmap()
                if (bitmap != null) bitmaps[name] = bitmap
            }
            entry = zip.nextEntry
        }
    }
    return bitmaps
}