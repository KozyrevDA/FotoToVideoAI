package utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Stable
val Int.tdp: TextUnit @Composable get() = textDp()

@Stable
val Double.tdp: TextUnit @Composable get() = textDp()

@Composable
private fun Int.textDp() = with(LocalDensity.current) { this@textDp.dp.toSp() }

@Composable
private fun Double.textDp() = with(LocalDensity.current) { this@textDp.dp.toSp() }