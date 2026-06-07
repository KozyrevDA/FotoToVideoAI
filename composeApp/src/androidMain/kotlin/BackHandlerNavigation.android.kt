import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackHandlerNavigation(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled, onBack)
}