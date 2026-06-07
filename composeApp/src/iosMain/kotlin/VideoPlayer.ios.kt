import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.Constants
import chaintech.videoplayer.model.PlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerView
import data.network.DEFAULT_IP
import org.jetbrains.compose.resources.vectorResource
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.ic_play

@Composable
actual fun VideoPlayer(
    idVideo: String?,
    accessToken: String?,
    isPlayingGlobal: Boolean,
    onReadyVideo: () -> Unit,
) {
    val videoUrl = Constants.VIDEOS_URL_IOS + idVideo + "?accessToken=" + accessToken
    val playerConfig = PlayerConfig(
        isPause = !isPlayingGlobal,
        pauseCallback = { paused -> },
        didEndVideo = onReadyVideo,
        isAutoHideControlEnabled = true,
        controlHideIntervalSeconds = 0,
        isPauseResumeEnabled = false,
        isSeekBarVisible = false,
        isDurationVisible = false,
        isMuteControlEnabled = false,
        isSpeedControlEnabled = false,
        isFullScreenEnabled = false,
        showDesktopControls = false,
    )
    var isPlaying by remember { mutableStateOf(isPlayingGlobal) }

    LaunchedEffect(isPlayingGlobal) {
        isPlaying = isPlayingGlobal
        playerConfig.isPause = !isPlayingGlobal
    }

    Box(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    isPlaying = !isPlaying
                    playerConfig.isPause = !isPlaying
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        VideoPlayerView(
            modifier = Modifier.fillMaxSize(),
            url = videoUrl,
            playerConfig = playerConfig
        )

        if (!isPlaying) {
            IconButton(
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.Center)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = .6F),
                        shape = CircleShape
                    ),
                onClick = {
                    isPlaying = true
                    playerConfig.isPause = false
                }
            ) {
                Icon(
                    modifier = Modifier.size(46.dp),
                    imageVector = vectorResource(Res.drawable.ic_play),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
actual fun VideoPlayer(
    templateName: String,
    isPlayingGlobal: Boolean,
    onReadyVideo: () -> Unit,
    onClickVideo: (() -> Unit)?,
    modifier: Modifier,
) {
    val videoUrl = DEFAULT_IP + templateName
    val playerConfig = PlayerConfig(
        isPause = !isPlayingGlobal,
        pauseCallback = { },
        didEndVideo = onReadyVideo,
        isAutoHideControlEnabled = true,
        controlHideIntervalSeconds = 0,
        isPauseResumeEnabled = false,
        isSeekBarVisible = false,
        isDurationVisible = false,
        isMuteControlEnabled = false,
        isSpeedControlEnabled = false,
        isFullScreenEnabled = false,
        showDesktopControls = false,
    )

    LaunchedEffect(isPlayingGlobal) {
        playerConfig.isPause = !isPlayingGlobal
    }

    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        VideoPlayerView(
            modifier = Modifier.fillMaxSize(),
            url = videoUrl,
            playerConfig = playerConfig
        )

        if (onClickVideo != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onClickVideo)
            )
        }
    }
}