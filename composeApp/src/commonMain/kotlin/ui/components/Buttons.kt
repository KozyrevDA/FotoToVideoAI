package ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.theme.GraphiteGray
import ui.theme.MidnightShadow
import ui.theme.MintDark
import ui.theme.MintGlow
import ui.theme.MintLight
import ui.theme.NeonGreen
import ui.theme.TropicalMint
import ui.theme.VeryDarkGray
import utils.extensions.tdp

@Composable
fun ButtonGreen(
    modifier: Modifier = Modifier,
    rounded: Dp = 12.dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(rounded)

    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = TropicalMint,
                    shape = shape
                )
                .clip(shape)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                content()
            }
        }
    }
}

@Composable
fun ButtonBottomNav(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    isActive: Boolean = false,
    onClick: () -> Unit,
) {
    var heightInPx by remember { mutableIntStateOf(0) }
    val interactionSource = remember { MutableInteractionSource() }
    val indication = ripple(
        bounded = true,
        color = Color.Gray,
        radius = with(LocalDensity.current) { (heightInPx / 2.3F).toDp() })

    Box(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1F),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 6.dp)
                .onGloballyPositioned { coordinates ->
                    heightInPx = coordinates.size.height
                }
                .clickable(
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = indication
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize().scale(.5F),
                imageVector = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (isActive) NeonGreen else GraphiteGray)
            )
        }
    }
}

@Composable
fun DarkSubButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(174.dp, 146.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(color = VeryDarkGray, shape = shape)
            .border(
                width = .9.dp,
                brush = Brush.linearGradient(
                    0.0F to Color.White.copy(alpha = 0.7F),
                    0.4F to Color.Transparent,
                    0.7F to Color.Transparent,
                    1.0F to Color.White.copy(alpha = 0.7F),
                    start = Offset(0F, 0F),
                    end = Offset.Infinite
                ),
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun DarkSubDiscountButton(
    discount: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(174.dp, 156.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .size(174.dp, 146.dp)
                .clip(shape)
                .background(color = VeryDarkGray, shape = shape)
                .border(
                    width = 1.4.dp,
                    color = MintDark,
                    shape = shape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        Box(
            modifier = Modifier
                .size(68.dp, 22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(MintGlow, MintLight, MintDark),
                        center = Offset.Unspecified,
                        radius = 100F
                    )
                )
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = discount,
                fontSize = 12.tdp,
                color = MidnightShadow,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GradientToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val width = 64.dp
    val height = 34.dp

    val thumbWidth = 34.dp
    val thumbHeight = 26.dp

    val padding = 4.dp

    val offset by animateDpAsState(
        targetValue = if (checked)
            width - thumbWidth - padding
        else
            padding,
        animationSpec = tween(200),
        label = "offset"
    )

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(50))
            .run {
                if (checked)
                    background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF7A00), // orange
                                Color(0xFFFF3D81), // pink
                                Color(0xFF8F00FF)  // purple
                            )
                        )
                    )
                else {
                    background(Color(0xFF1C1F24))
                }
            }
            .clickable { onCheckedChange(!checked) }
            .padding(padding)
    ) {
        Box(
            modifier = Modifier
                .offset(x = offset)
                .size(thumbWidth, thumbHeight)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        )
    }
}