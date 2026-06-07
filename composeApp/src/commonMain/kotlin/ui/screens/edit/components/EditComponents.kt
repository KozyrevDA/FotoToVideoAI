package ui.screens.edit.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.generation_in_progress
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.stringResource
import ui.theme.BackgroundDark
import ui.theme.ProgressEnd
import ui.theme.ProgressStart
import ui.theme.WhiteAlpha
import utils.extensions.tdp
import kotlin.random.Random

@Composable
fun ProgressGeneration(modifier: Modifier = Modifier.fillMaxWidth()) {
    var progress by remember { mutableStateOf(0F) }
    val animationProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200),
        label = "animation_generation"
    )

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1500L)
            val progressTemp = Random.nextInt(1, 10) / 100F
            if (progress + progressTemp <= 1F) {
                progress += progressTemp
            } else 1F
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RoundedProgressBar(progress = animationProgress.value)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.generation_in_progress),
                fontSize = 16.tdp,
                lineHeight = 17.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                textAlign = TextAlign.Start
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 16.tdp,
                lineHeight = 17.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun RoundedProgressBar(
    progress: Float,
    backgroundColor: Color = BackgroundDark,
    progressColorStart: Color = ProgressStart,
    progressColorEnd: Color = ProgressEnd,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val shape = RoundedCornerShape(50.dp)
    val animatedProgress = animateFloatAsState(
        targetValue = progress.coerceIn(0F, 1F),
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )

    Box(
        modifier = modifier
            .clip(shape = shape)
            .background(backgroundColor, shape)
            .border(
                width = .8.dp,
                color = WhiteAlpha,
                shape = shape
            )
            .height(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress.value)
                .padding(2.dp)
                .clip(shape = shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(progressColorStart, progressColorEnd)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress.value)
                .padding(3.dp)
                .clip(shape = shape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3F),
                            Color.Transparent
                        ),
                        center = Offset.Unspecified,
                        radius = 200F
                    )
                )
        )
    }
}