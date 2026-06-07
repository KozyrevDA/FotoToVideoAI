import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.compose.PlayerSurface
import app.Constants
import data.network.DEFAULT_IP
import org.jetbrains.compose.resources.vectorResource
import org.nla.phototovideoai.utils.ExoCacheProvider
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.ic_play

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    idVideo: String?,
    accessToken: String?,
    isPlayingGlobal: Boolean,
    onReadyVideo: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cache = remember { ExoCacheProvider.getCache(context, "results_cache") }
    val dataSourceFactory = DefaultDataSource.Factory(
        context,
        DefaultHttpDataSource.Factory().apply {
            accessToken?.let {
                setDefaultRequestProperties(mapOf("Authorization" to "Bearer $it"))
            }
        }
    )
    val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(dataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    val mediaSource = remember(idVideo, accessToken) {
        if (accessToken != null && idVideo != null) {
            ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Constants.VIDEOS_URL + idVideo))
        } else {
            null
        }
    }
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            mediaSource?.let { setMediaSource(it) }
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }
    var isPlaying by remember { mutableStateOf(isPlayingGlobal) }

    LaunchedEffect(isPlayingGlobal) {
        if (isPlayingGlobal) player.play() else player.pause()
        isPlaying = isPlayingGlobal
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP,
                    -> {
                    player.pause()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) onReadyVideo()
            }

            override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                isPlaying = isPlayingNow
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    if (isPlaying) {
                        player.pause()
                    } else {
                        player.play()
                    }
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        PlayerSurface(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(9F / 16F),
            player = player
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
                onClick = { player.play() }
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

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    templateName: String,
    isPlayingGlobal: Boolean,
    onReadyVideo: () -> Unit,
    onClickVideo: (() -> Unit)?,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
        }
    }

    LaunchedEffect(templateName) {
        val upstreamFactory = DefaultDataSource.Factory(context)
        val cache = ExoCacheProvider.getCache(context, "templates_cache")
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(
                MediaItem.fromUri(DEFAULT_IP + templateName)
            )

        player.setMediaSource(mediaSource)
        player.prepare()
    }

    LaunchedEffect(isPlayingGlobal) {
        if (isPlayingGlobal) player.play() else player.pause()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isPlayingGlobal) player.play()
                }

                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP,
                    -> {
                    player.pause()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    onReadyVideo()
                }
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    Box(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        PlayerSurface(
            modifier = Modifier.fillMaxSize(),
            player = player
        )
    }
}