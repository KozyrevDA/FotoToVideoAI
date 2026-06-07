package com.msilimon.cropper.cropper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.msilimon.cropper.shapes.rectangleShape
import com.msilimon.cropper.utils.CroppingUtils.calculateCroppingShapePositionWhenWindowResized
import com.msilimon.cropper.utils.CroppingUtils.calculateCroppingShapeSizeWhenWindowResized
import com.msilimon.cropper.utils.CroppingUtils.checkIfTouchInCroppingShape
import com.msilimon.cropper.utils.CroppingUtils.detectTouchedSide
import com.msilimon.cropper.utils.CroppingUtils.resizeShapeWhenDrag
import com.msilimon.cropper.utils.TouchedSide

@Composable
internal fun CroppingShape(
    scaleImage: Float,
    aspectRatio: Float,
    croppingShapeStrokeWidth: Float,
    croppingShapeStrokeColor: Color,
    gridStrokeWidth: Float,
    gridStrokeColor: Color,
    showGridLines: Boolean,
    backGroundAlpha: Float,
    square: Boolean,
    onChangeWindowSize: (croppingRectSize: Size, croppingRectPosition: Offset, windowSize: Size) -> Unit
) {
    var croppingRectSize by remember { mutableStateOf(Size(-1f, -1f)) }
    var croppingRectPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var isTouchingTheCroppingShape by remember { mutableStateOf(false) }
    var previousWindowSize by remember { mutableStateOf(Size(-1f, -1f)) }

    var touchedSide by remember { mutableStateOf(TouchedSide.NONE) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(aspectRatio)
            .scale(scaleImage)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    val resizedArgs = resizeShapeWhenDrag(
                        touchedSide,
                        croppingRectSize,
                        croppingRectPosition,
                        croppingShapeStrokeWidth,
                        Size(size.width.toFloat(), size.height.toFloat()),
                        pan,
                        square
                    )
                    if (isTouchingTheCroppingShape) {
                        croppingRectSize = resizedArgs.first
                        croppingRectPosition = resizedArgs.second
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isTouchingTheCroppingShape =
                            checkIfTouchInCroppingShape(it, croppingRectSize, croppingRectPosition)

                        // Determine which side is touched based on the touch point
                        val touchX = it.x - croppingRectPosition.x
                        val touchY = it.y - croppingRectPosition.y
                        touchedSide = detectTouchedSide(
                            touchX,
                            touchY,
                            croppingRectSize,
                            croppingShapeStrokeWidth
                        )
                        awaitRelease()
                        touchedSide = TouchedSide.NONE
                    }
                )
            }
    ) {
        if (previousWindowSize == Size(-1f, -1f)) previousWindowSize = size
        if (croppingRectSize == Size(-1f, -1f)) {
            croppingRectSize = if (square) {
                if (aspectRatio < 1F)
                    size.copy(height = size.width)
                else
                    size.copy(width = size.height)
            } else size
        }
        if (previousWindowSize != size) {
            croppingRectSize =
                calculateCroppingShapeSizeWhenWindowResized(
                    previousWindowSize,
                    size,
                    croppingRectSize
                )
            croppingRectPosition =
                calculateCroppingShapePositionWhenWindowResized(
                    previousWindowSize,
                    size,
                    croppingRectPosition
                )
            previousWindowSize = size

        }
        onChangeWindowSize(
            croppingRectSize,
            croppingRectPosition,
            size
        )
        rectangleShape(
            croppingShapeSize = croppingRectSize,
            croppingShapePosition = croppingRectPosition,
            croppingShapeStrokeWidth = croppingShapeStrokeWidth,
            croppingShapeStrokeColor = croppingShapeStrokeColor,
            showGridLines = showGridLines,
            gridStrokeWidth = gridStrokeWidth,
            gridStrokeColor = gridStrokeColor,
            backGroundAlpha = backGroundAlpha
        )
    }
}