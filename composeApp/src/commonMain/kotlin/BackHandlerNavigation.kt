import androidx.compose.runtime.Composable

//Временное решение, пока официальная библиотека поддерживает BackHandler только для Android

@Composable
expect fun BackHandlerNavigation(enabled: Boolean = true, onBack: () -> Unit)