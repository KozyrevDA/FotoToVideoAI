import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import gun0912.tedimagepicker.builder.TedImagePicker

class AndroidImagePicker : ImagePicker {
    @Composable
    override fun PickImageFromGallery(
        showPicker: MutableState<Boolean>,
        onPicked: (ImageBitmap) -> Unit,
    ) {
        val context = LocalContext.current

        LaunchedEffect(showPicker.value) {
            if (showPicker.value) {
                TedImagePicker
                    .with(context)
                    .start { uri ->
                        val bitmap = decodeBitmapSafely(context, uri)
                        if (bitmap != null) {
                            onPicked(bitmap)
                        }
                        showPicker.value = false
                    }
            }
        }
    }

    @Composable
    override fun RequestGalleryPermission(onGranted: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val granted = result.values.all { it }
            if (granted) {
                onGranted()
            }
        }

        LaunchedEffect(Unit) {
            launcher.launch(permission)
        }
    }

    private fun decodeBitmapSafely(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
    ): ImageBitmap? {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)
                ?.use { BitmapFactory.decodeStream(it, null, options) }

            var scale = 1
            while (options.outWidth / scale > maxWidth || options.outHeight / scale > maxHeight) {
                scale *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = scale }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOptions)?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }
}

actual fun getImagePicker(): ImagePicker? = AndroidImagePicker()