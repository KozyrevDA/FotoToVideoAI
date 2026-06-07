package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.close
import phototovideoai.composeapp.generated.resources.trim
import com.msilimon.cropper.cropper.ImageCropper
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ui.theme.White
import utils.extensions.tdp

@Composable
fun ImageCropperFullScreen(
    imageBitmap: ImageBitmap,
    onClickClose: () -> Unit,
    onCroppedImageBitmap: (ImageBitmap) -> Unit,
) {
    var crop by remember { mutableStateOf(false) }
    var isCropping by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                enabled = false,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                ButtonGreen(
                    modifier = Modifier
                        .size(110.dp, 52.dp)
                        .align(Alignment.Center),
                    onClick = { crop = true }
                ) {
                    Text(
                        text = stringResource(Res.string.trim),
                        fontSize = 16.tdp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 16.dp, end = 4.dp)
                        .size(52.dp),
                    onClick = onClickClose
                ) {
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(Res.drawable.close),
                        colorFilter = ColorFilter.tint(color = White),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            ImageCropper(
                modifier = Modifier.fillMaxSize(.84F),
                image = imageBitmap,
                onCropStart = { isCropping = true },
                onCropSuccess = {
                    onCroppedImageBitmap(it)
                    isCropping = false
                    crop = false
                },
                crop = crop,
                square = true
            )
        }

        if (isCropping)
            CircularProgressIndicator(color = White)
    }
}