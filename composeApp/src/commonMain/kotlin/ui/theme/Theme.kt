package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val DarkColorScheme = darkColorScheme(
    primary = DeepPurple,
    secondary = NeonGreen,
    tertiary = MangoOrange,
    background = MidnightShadow,
    onBackground = White
)

val LightColorScheme = lightColorScheme(
    primary = DeepPurple,
    secondary = NeonGreen,
    tertiary = MangoOrange,
    background = White,
    onBackground = DarkGray
)

@Composable
fun PhotoToVideoAiTheme(
    //darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (darkTheme) {
        true -> DarkColorScheme
        false -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}