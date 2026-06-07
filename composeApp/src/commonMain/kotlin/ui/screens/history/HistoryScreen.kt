package ui.screens.history

import Platform
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import app.Constants
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import data.model.Video
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState.Granted
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import getFirebaseKMP
import getImagePicker
import getPlatform
import getSdkVersion
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.create
import phototovideoai.composeapp.generated.resources.create_first_generation
import phototovideoai.composeapp.generated.resources.download
import phototovideoai.composeapp.generated.resources.error
import phototovideoai.composeapp.generated.resources.fullscreen
import phototovideoai.composeapp.generated.resources.history
import phototovideoai.composeapp.generated.resources.ic_menu_gen
import phototovideoai.composeapp.generated.resources.ic_play
import phototovideoai.composeapp.generated.resources.loading
import phototovideoai.composeapp.generated.resources.no_generation
import phototovideoai.composeapp.generated.resources.onb_back
import phototovideoai.composeapp.generated.resources.trash
import ui.components.TopBar
import ui.navigation.AppNavigationActions
import ui.screens.edit.components.DeleteVideoHint
import ui.screens.edit.normal.WriteStoragePermission
import ui.theme.MidnightShadow
import ui.theme.White
import utils.events.Events
import utils.extensions.tdp

@Composable
fun HistoryScreen(
    navigationActions: AppNavigationActions,
    showBottomBar: MutableState<Boolean>,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val toastString = stringResource(Res.string.loading)
    val countCoins = viewModel.countCoins.collectAsState(Dispatchers.Main)
    val isProSubState = viewModel.isProSub.collectAsState(Dispatchers.Main)
    val generationIsProcessingState = viewModel.generationIsProcessingState
        .collectAsState(Dispatchers.Main)
    val videos by viewModel.videos.collectAsState(Dispatchers.Main)
    val videosDownloadedState by viewModel.videosDownloaded.collectAsState(Dispatchers.Main)
    var writeStoragePermission by remember { mutableStateOf(false) }
    var showDeleteVideoHint by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.updateCoins()
        viewModel.updateProSub()
    }

    LifecycleResumeEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.updateVideos()
        viewModel.updateDownloadedVideos()

        onPauseOrDispose {
            showDeleteVideoHint = null
            showBottomBar.value = true
        }
    }

    HistoryScreenContent(
        videos = videos,
        videosDownloaded = videosDownloadedState,
        accessToken = viewModel.accessToken ?: "",
        isPro = isProSubState.value,
        countCoins = countCoins.value,
        generationIsProcessing = generationIsProcessingState.value,
        onClickGen = {
            navigationActions.navigateToListScreen()
            Events.put(Events.SCREEN_5_HISTORY_GEN)
        },
        onClickCountCoins = {
            navigationActions.navigateToPaywallScreen(prevScreen = "экран_истории")
            Events.put(Events.SCREEN_5_HISTORY_TO_SUB)
        },
        onClickVideoItem = {
            navigationActions.navigateToResultVideoScreen(it.idVideo)
            Events.put(Events.SCREEN_5_HISTORY_CLICK_IMAGE)
        },
        onClickDelete = {
            showBottomBar.value = false
            showDeleteVideoHint = it.idVideo
        },
        onClickDownload = {
            viewModel.downloadVideo(
                video = it,
                toastString = toastString
            )
        },
        onClickFullScreen = {
            navigationActions.navigateToResultVideoScreen(idVideo = it.idVideo)
        },
    )

    val sdkAndroid = getSdkVersion()?.number
    if (sdkAndroid != null && sdkAndroid <= 28) {
        WriteStoragePermission {
            writeStoragePermission = true
        }
    } else {
        writeStoragePermission = true
    }

    if (writeStoragePermission) {
        RequestPermissionGallery()
    }

    showDeleteVideoHint?.let {
        DeleteVideoHint(
            onClickDelete = {
                showBottomBar.value = true
                showDeleteVideoHint = null
                viewModel.deleteVideo(it)
            },
            onClickBack = {
                showBottomBar.value = true
                showDeleteVideoHint = null
            }
        )
    }
}

@Composable
private fun HistoryScreenContent(
    videos: List<Video>?,
    videosDownloaded: List<String>,
    accessToken: String,
    isPro: Boolean,
    countCoins: Int?,
    generationIsProcessing: Boolean?,
    onClickGen: () -> Unit,
    onClickCountCoins: () -> Unit,
    onClickVideoItem: (Video) -> Unit,
    onClickDelete: (Video) -> Unit,
    onClickDownload: (Video) -> Unit,
    onClickFullScreen: (Video) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.4F)
                .align(Alignment.TopCenter)
                .alpha(.2F),
            painter = painterResource(Res.drawable.onb_back),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Top(
                isPro = isPro,
                countCoins = countCoins,
                onClickCountCoins = onClickCountCoins
            )

            if (videos == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
                }
            } else if (videos.isEmpty()) {
                NotGenerationsPanel(onClickGen = onClickGen)
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (generationIsProcessing == true) {
                        item {
                            VideoItemProcessing()
                        }
                    }
                    items(videos) { video ->
                        VideoItem(
                            video = video,
                            videosDownloaded = videosDownloaded,
                            accessToken = accessToken,
                            onClick = onClickVideoItem,
                            onClickDelete = onClickDelete,
                            onClickDownload = onClickDownload,
                            onClickFullScreen = onClickFullScreen,
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(90.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun Top(
    isPro: Boolean,
    countCoins: Int?,
    onClickCountCoins: () -> Unit,
    title: String = stringResource(Res.string.history),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.tdp
        )

        TopBar(
            isPro = isPro,
            countCoins = countCoins,
            onClick = onClickCountCoins
        )
    }
}

@Composable
private fun NotGenerationsPanel(
    onClickGen: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(.9F)
        .padding(horizontal = 60.dp),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.no_generation),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.tdp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.create_first_generation),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
            fontSize = 16.tdp,
            lineHeight = 17.tdp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(37.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MidnightShadow
            ),
            onClick = onClickGen
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.size(18.dp),
                        imageVector = vectorResource(Res.drawable.ic_menu_gen),
                        colorFilter = ColorFilter.tint(color = Color.Black),
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(Res.string.create),
                        fontSize = 18.tdp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun VideoItem(
    video: Video,
    videosDownloaded: List<String>,
    accessToken: String,
    onClick: (Video) -> Unit,
    onClickDelete: (Video) -> Unit,
    onClickDownload: (Video) -> Unit,
    onClickFullScreen: (Video) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F),
) {
    Box(modifier = modifier) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(Constants.THUMBNAILS_URL.plus(video.idVideo))
                .httpHeaders(
                    headers = NetworkHeaders.Builder()
                        .set("Authorization", "Bearer $accessToken")
                        .build()
                )
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = White)
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(Res.string.error))
                }
            },
            contentDescription = null,
        )

        if (!videosDownloaded.contains(video.idVideo)) {
            IconButton(
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.TopStart)
                    .padding(top = 10.dp, start = 10.dp),
                onClick = { onClickDownload(video) }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(Res.drawable.download),
                    contentDescription = null
                )
            }
        }

        IconButton(
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 10.dp),
            onClick = { onClickDelete(video) }
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.trash),
                contentDescription = null
            )
        }

        IconButton(
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 10.dp, end = 10.dp),
            onClick = { onClickFullScreen(video) }
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.fullscreen),
                contentDescription = null
            )
        }

        IconButton(
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.Center)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = .6F),
                    shape = CircleShape
                ),
            onClick = { onClick(video) }
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

@Composable
private fun LazyGridItemScope.VideoItemProcessing(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F),
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = White)
    }
}

@Composable
private fun RequestPermissionGallery() {
    when (getPlatform().name) {
        Platform.Name.ANDROID -> getImagePicker()?.RequestGalleryPermission { }
        Platform.Name.IOS -> {
            val lifecycleOwner = LocalLifecycleOwner.current
            val permissionFactory = rememberPermissionsControllerFactory()
            val permissionController = remember(permissionFactory) {
                permissionFactory.createPermissionsController()
            }

            BindEffect(permissionController)

            LaunchedEffect(lifecycleOwner) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    try {
                        withFrameNanos {}
                        when (permissionController.getPermissionState(Permission.GALLERY)) {
                            Granted -> {}
                            else -> permissionController.providePermission(Permission.GALLERY)
                        }
                    } catch (e: Exception) {
                        getFirebaseKMP().recordException(e)
                    }
                }
            }
        }
    }
}