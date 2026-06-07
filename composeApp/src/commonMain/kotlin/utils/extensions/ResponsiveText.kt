package utils.extensions

import Platform
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import getPlatform

private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.96F

@Composable
fun ResponsiveText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color? = null,
    textAlign: TextAlign = TextAlign.Center,
    style: TextStyle,
    targetTextSizeHeight: TextUnit = style.fontSize,
    maxLines: Int = 1,
    fontSize: (TextUnit) -> Unit = {},
    drawContent: (Boolean) -> Unit = {},
) {
    val platform = getPlatform().name
    var textSize by remember { mutableStateOf(targetTextSizeHeight) }
    var shouldDraw by remember { mutableStateOf(false) }

    Text(
        modifier = modifier.drawWithContent {
            if (shouldDraw) {
                drawContent()
                drawContent(true)
            }
        },
        text = text,
        color = color ?: style.color,
        textAlign = textAlign,
        fontSize = textSize,
        fontFamily = style.fontFamily,
        fontStyle = style.fontStyle,
        fontWeight = style.fontWeight,
        lineHeight = style.lineHeight,
        maxLines = if (platform == Platform.Name.ANDROID) maxLines else maxLines + 1,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { textLayoutResult ->
            when (platform) {
                Platform.Name.ANDROID -> {
                    val maxCurrentLineIndex: Int = textLayoutResult.lineCount - 1

                    if (textLayoutResult.isLineEllipsized(maxCurrentLineIndex)) {
                        textSize = textSize.times(TEXT_SCALE_REDUCTION_INTERVAL)
                        fontSize(textSize)
                    } else {
                        shouldDraw = true
                    }
                }

                Platform.Name.IOS -> {
                    if (textLayoutResult.lineCount > maxLines) {
                        textSize = textSize.times(TEXT_SCALE_REDUCTION_INTERVAL)
                        fontSize(textSize)
                    } else {
                        shouldDraw = true
                    }
                }
            }
        },
    )
}