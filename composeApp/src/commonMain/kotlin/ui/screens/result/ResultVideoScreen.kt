package ui.screens.result

import Platform
import ShareProvider
import VideoPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.Constants
import getPlatform
import getShareProvider
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.close_button
import phototovideoai.composeapp.generated.resources.created_in_app
import phototovideoai.composeapp.generated.resources.download_button
import phototovideoai.composeapp.generated.resources.ic_instagram_logo
import phototovideoai.composeapp.generated.resources.ic_tg_logo
import phototovideoai.composeapp.generated.resources.ic_vk
import phototovideoai.composeapp.generated.resources.ic_whatsapp_logo
import phototovideoai.composeapp.generated.resources.loading
import phototovideoai.composeapp.generated.resources.max_logo
import phototovideoai.composeapp.generated.resources.share_button
import phototovideoai.composeapp.generated.resources.share_via
import phototovideoai.composeapp.generated.resources.trash
import ui.navigation.AppNavigationActions
import ui.screens.edit.components.DeleteVideoHint
import ui.theme.PhotoToVideoAiTheme
import utils.events.Events
import utils.extensions.tdp

@Composable
fun ResultVideoScreen(
    navigationActions: AppNavigationActions,
    idVideo: String,
    viewModel: ResultVideoViewModel = koinViewModel(),
) {
    val toastString = stringResource(Res.string.loading)
    val videosDownloadedState by viewModel.videosDownloaded.collectAsState(Dispatchers.Main)
    val shareVideoPathState by viewModel.shareVideoPath.collectAsState(Dispatchers.Main)
    var showDeleteVideoHint by remember { mutableStateOf(false) }
    var showSharePanel by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.updateDownloadedVideos()
    }

    ResultVideoScreenContent(
        idVideo = idVideo,
        videosDownloaded = videosDownloadedState,
        accessToken = viewModel.accessToken ?: "",
        onClickClose = {
            navigationActions.back()
            Events.put(Events.SCREEN_6_RESULT_CLOSE)
        },
        onClickDownload = {
            viewModel.downloadVideo(idVideo = idVideo, toastString = toastString)
        },
        onClickShare = {
            viewModel.shareVideo(idVideo = idVideo, toastString = toastString)
            showSharePanel = true
        },
        onClickDelete = {
            showDeleteVideoHint = true
        }
    )

    if (showDeleteVideoHint) {
        DeleteVideoHint(
            onClickDelete = {
                showDeleteVideoHint = false
                viewModel.deleteVideo(idVideo)
                navigationActions.back()
            },
            onClickBack = { showDeleteVideoHint = false }
        )
    }

    if (showSharePanel) {
        shareVideoPathState?.let {
            SharePanel(
                shareVideoPath = it,
                shareProvider = getShareProvider(),
                platform = getPlatform().name,
                isInstalledFromRuStore = viewModel.isInstalledFromRuStore,
                onClickCloseShare = {
                    showSharePanel = false
                }
            )
        }
    }
}

@Composable
private fun ResultVideoScreenContent(
    idVideo: String,
    videosDownloaded: List<String>,
    accessToken: String,
    onClickClose: () -> Unit,
    onClickDownload: () -> Unit,
    onClickShare: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Transparent),
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        VideoPlayer(
            idVideo = idVideo,
            accessToken = accessToken,
            isPlayingGlobal = true,
            onReadyVideo = {}
        )

        ButtonsPlayer(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxHeight()
                .height(60.dp)
                .align(Alignment.TopEnd)
                .padding(top = 50.dp, end = 16.dp),
            isVisibleDownload = !videosDownloaded.contains(idVideo),
            onClickClose = onClickClose,
            onClickDownload = onClickDownload,
            onClickShare = onClickShare,
            onClickDelete = onClickDelete,
        )
    }
}

@Composable
private fun ButtonsPlayer(
    isVisibleDownload: Boolean,
    onClickClose: () -> Unit,
    onClickDownload: () -> Unit,
    onClickShare: () -> Unit,
    onClickDelete: () -> Unit,
    modifier: Modifier = Modifier.fillMaxHeight(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(
            modifier = Modifier.size(50.dp),
            onClick = { onClickClose() }
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.close_button),
                contentDescription = null
            )
        }

        HorizontalDivider(
            modifier = Modifier.width(40.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .2F)
        )

        if (isVisibleDownload) {
            IconButton(
                modifier = Modifier.size(50.dp),
                onClick = { onClickDownload() }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(Res.drawable.download_button),
                    contentDescription = null
                )
            }
        }

        IconButton(
            modifier = Modifier.size(50.dp),
            onClick = { onClickShare() }
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.share_button),
                contentDescription = null
            )
        }

        IconButton(
            modifier = Modifier.size(50.dp),
            onClick = { onClickDelete() }
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.trash),
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SharePanel(
    shareVideoPath: String,
    shareProvider: ShareProvider?,
    platform: Platform.Name,
    isInstalledFromRuStore: Boolean?,
    onClickCloseShare: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val shareCaption = stringResource(Res.string.created_in_app).run {
        when (platform) {
            Platform.Name.IOS -> plus(Constants.URL_APP_APPSTORE)
            Platform.Name.ANDROID -> {
                if (isInstalledFromRuStore == true) {
                    plus(Constants.URL_APP_RUSTORE)
                } else {
                    plus(Constants.URL_APP_GOOGLE_PLAY)
                }
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.5F)
                .background(color = MaterialTheme.colorScheme.background.copy(alpha = .96F))
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                modifier = Modifier.size(50.dp)
                    .align(Alignment.End)
                    .padding(
                        top = 12.dp,
                        end = 12.dp
                    ),
                onClick = { onClickCloseShare() }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(Res.drawable.close_button),
                    contentDescription = null
                )
            }

            Text(
                text = stringResource(Res.string.share_via),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.tdp
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        shareProvider?.readBytesFromUri(uri = shareVideoPath)?.let { bytes ->
                            shareProvider.shareToTelegram(
                                bytes = bytes,
                                caption = shareCaption
                            )
                        }
                        Events.put(Events.SCREEN_6_RESULT_SHARE_TELEGRAM)
                    }
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.ic_tg_logo),
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = {
                        shareProvider?.readBytesFromUri(uri = shareVideoPath)?.let { bytes ->
                            shareProvider.shareToWhatsApp(
                                bytes = bytes,
                                caption = shareCaption
                            )
                        }
                        Events.put(Events.SCREEN_6_RESULT_SHARE_WHATSAPP)
                    }
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.ic_whatsapp_logo),
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = {
                        shareProvider?.readBytesFromUri(uri = shareVideoPath)?.let { bytes ->
                            shareProvider.shareToMax(
                                bytes = bytes,
                                caption = shareCaption
                            )
                        }
                        Events.put(Events.SCREEN_6_RESULT_SHARE_MAX)
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.max_logo),
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = {
                        shareProvider?.readBytesFromUri(uri = shareVideoPath)?.let { bytes ->
                            shareProvider.shareToVK(
                                bytes = bytes,
                                caption = shareCaption
                            )
                        }
                        Events.put(Events.SCREEN_6_RESULT_SHARE_VK)
                    }
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.ic_vk),
                        contentDescription = null
                    )
                }

                IconButton(
                    onClick = {
                        shareProvider?.readBytesFromUri(uri = shareVideoPath)?.let { bytes ->
                            shareProvider.shareToInstagram(
                                bytes = bytes,
                                caption = shareCaption
                            )
                        }
                        Events.put(Events.SCREEN_6_RESULT_SHARE_INSTAGRAM)
                    }
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.ic_instagram_logo),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ResultImageScreenContentPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResultVideoScreenContent(
                idVideo = "",
                videosDownloaded = listOf(),
                accessToken = "",
                onClickClose = {},
                onClickShare = {},
                onClickDelete = {},
                onClickDownload = {}
            )
        }
    }
}