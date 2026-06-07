import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(
    idVideo: String? = null,
    accessToken: String? = null,
    isPlayingGlobal: Boolean,
    onReadyVideo: () -> Unit,
)

@Composable
expect fun VideoPlayer(
    templateName: String,
    isPlayingGlobal: Boolean,
    onReadyVideo: () -> Unit,
    onClickVideo: (() -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxSize(),
)