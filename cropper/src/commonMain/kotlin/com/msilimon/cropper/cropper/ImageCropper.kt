package com.msilimon.cropper.cropper

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.msilimon.cropper.utils.CroppingUtils.cropAndShowImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@Composable
fun ImageCropper(
    image: ImageBitmap,
    onCropStart: () -> Unit,
    onCropSuccess: (ImageBitmap) -> Unit,
    croppingShapeStrokeWidth: Float = 2f,
    croppingShapeStrokeColor: Color = Color.White,
    gridStrokeWidth: Float = 1f,
    gridStrokeColor: Color = Color.White,
    showGridLines: Boolean = true,
    backGroundAlpha: Float = 0.7f,
    crop: Boolean = false,
    square: Boolean = false,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var croppingRectSize by remember { mutableStateOf(Size(1f, 1f)) }
    var croppingRectPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var windowSize by remember { mutableStateOf(Size(0f, 0f)) }
    var scaleImage by remember { mutableFloatStateOf(1F) }
    val density = LocalDensity.current
    val aspectRatio = image.width / image.height.toFloat()

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(aspectRatio)
                .scale(scaleImage)
                .onGloballyPositioned { coordinates ->
                    with(density) {
                        if (coordinates.size.height.toDp() > maxHeight) {
                            scaleImage *= .9F
                        }
                    }
                },
            contentScale = ContentScale.Fit
        )
        if (crop) {
            Crop(
                image,
                croppingRectSize,
                windowSize,
                croppingRectPosition,
                density,
                crop, onCropStart, onCropSuccess
            )
        }
        CroppingShape(
            croppingShapeStrokeWidth = croppingShapeStrokeWidth,
            croppingShapeStrokeColor = croppingShapeStrokeColor,
            gridStrokeWidth = gridStrokeWidth,
            gridStrokeColor = gridStrokeColor,
            showGridLines = showGridLines,
            backGroundAlpha = backGroundAlpha,
            aspectRatio = aspectRatio,
            scaleImage = scaleImage,
            square = square
        ) { size, offset, window ->
            croppingRectSize = size
            croppingRectPosition = offset
            density.run {
                windowSize = Size(
                    window.width.dp.roundToPx().toFloat(),
                    window.height.dp.roundToPx().toFloat()
                )
            }
        }
    }
}

@Composable
private fun Crop(
    image: ImageBitmap,
    croppingShapeSize: Size,
    windowSize: Size,
    croppingShapePosition: Offset,
    density: Density,
    crop: Boolean,
    onCropStart: () -> Unit,
    onCropSuccess: (ImageBitmap) -> Unit,

    ) {
    LaunchedEffect(crop) {
        if (crop) {
            flow {
                val croppedImageBitmap = cropAndShowImage(
                    image,
                    croppingShapeSize,
                    croppingShapePosition,
                    windowSize, density
                )
                emit(croppedImageBitmap)
            }
                .flowOn(Dispatchers.Default)
                .onStart {
                    onCropStart()
                    delay(400)
                }
                .onEach {
                    onCropSuccess(it)
                }
                .launchIn(this)
        }
    }
}