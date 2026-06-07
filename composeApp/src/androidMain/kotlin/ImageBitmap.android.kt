import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toPngByteArray(): ByteArray {
    val bitmap: Bitmap = asAndroidBitmap()
    val stream = ByteArrayOutputStream()
    bitmap.compress(CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
}