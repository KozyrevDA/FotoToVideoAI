import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap

interface ImagePicker {
    @Composable
    fun PickImageFromGallery(
        showPicker: MutableState<Boolean>,
        onPicked: (ImageBitmap) -> Unit,
    )

    @Composable
    fun RequestGalleryPermission(onGranted: () -> Unit)
}

expect fun getImagePicker(): ImagePicker?