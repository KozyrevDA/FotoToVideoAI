package data.model

import androidx.compose.ui.graphics.painter.Painter
import ui.components.Segment

data class PageStandard(
    val number: Int,
    val segment: Segment = Segment(number),
    val onbBackImage: Painter,
    val onbImage: Painter,
    val title: String,
    val desc1: String,
)