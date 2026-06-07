package ui.screens.list

import Language
import Platform
import VideoPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import app.Constants
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import data.model.Template
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import getFirebaseKMP
import getLanguage
import getPlatform
import kotlinx.coroutines.Dispatchers
import openUrl
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.before_and_after_str2
import phototovideoai.composeapp.generated.resources.error
import phototovideoai.composeapp.generated.resources.ic_launcher_playstore_avatar
import phototovideoai.composeapp.generated.resources.ic_name_app
import phototovideoai.composeapp.generated.resources.onb_back
import phototovideoai.composeapp.generated.resources.onb_back_bottom
import phototovideoai.composeapp.generated.resources.photo_by_example
import phototovideoai.composeapp.generated.resources.video_avatar_for_blog
import phototovideoai.composeapp.generated.resources.video_from_prompt_no_photo
import ui.components.TopBar
import ui.navigation.AppNavigationActions
import ui.theme.MidnightShadow
import ui.theme.ObsidianInk
import ui.theme.PhotoToVideoAiTheme
import ui.theme.White
import ui.theme.WhiteAlpha
import utils.events.Events
import utils.extensions.drawableRemote
import utils.extensions.tdp

@Composable
fun ListScreen(
    navigationActions: AppNavigationActions,
    viewModel: ListViewModel = koinViewModel(),
) {
    val countCoins = viewModel.countCoins.collectAsState(Dispatchers.Main)
    val isProSubState = viewModel.isProSub.collectAsState(Dispatchers.Main)
    val templatesMapState = viewModel.templatesMap.collectAsState(Dispatchers.Main)

    LaunchedEffect(Unit) {
        viewModel.updateCoins()
        viewModel.updateProSub()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ListScreenContent(
            isPro = isProSubState.value,
            countCoins = countCoins.value,
            templatesMap = templatesMapState.value,
            isInstalledFromRuStore = viewModel.isInstalledFromRuStore,
            onClickTemplate = { filter ->
                navigationActions.navigateToEditScreen(nameFilter = filter.path.substringAfterLast("/"))
                Events.put(Events.SCREEN_3_TO_FILTER)
            },
            onClickUserTemplate = { type ->
                when (type) {
                    0 -> {
                        navigationActions.navigateToEditScreen("animatePhoto")
                        Events.put(Events.SCREEN_3_TO_ANIMATE_PHOTO)
                    }

                    1 -> {
                        navigationActions.navigateToEditScreen("beforeAfter")
                        Events.put(Events.SCREEN_3_TO_BEFORE_AFTER)
                    }

                    2 -> {
                        navigationActions.navigateToEditScreen("videoAvatar")
                        Events.put(Events.SCREEN_3_TO_VIDEO_AVATAR)
                    }

                    3 -> {
                        navigationActions.navigateToEditScreen("videoFromPrompt")
                        Events.put(Events.SCREEN_3_TO_VIDEO_PROMPT)
                    }
                }
            },
            onClickCountCoins = {
                navigationActions.navigateToPaywallScreen(prevScreen = "экран_списка_фильтров")
                Events.put(Events.SCREEN_3_TO_SUB)
            }
        )
    }

    RequestPermissionNotification()
}

@Composable
private fun ListScreenContent(
    isPro: Boolean,
    countCoins: Int?,
    templatesMap: Map<String, List<Template>>,
    isInstalledFromRuStore: Boolean?,
    onClickTemplate: (Template) -> Unit,
    onClickUserTemplate: (Int) -> Unit,
    onClickCountCoins: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(color = MidnightShadow),
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

        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.2F)
                .align(Alignment.BottomCenter),
            painter = painterResource(Res.drawable.onb_back_bottom),
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
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn {
                item {
                    UsersTemplates(
                        onClickUserTemplates = onClickUserTemplate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                itemsIndexed(templatesMap.entries.toList()) { index, entry ->
                    FiltersRow(
                        title = entry.key,
                        templates = entry.value,
                        isInstalledFromRuStore = isInstalledFromRuStore,
                        onClickTemplate = onClickTemplate
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }
    }
}

@Composable
private fun Top(
    isPro: Boolean,
    countCoins: Int?,
    onClickCountCoins: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = vectorResource(Res.drawable.ic_name_app),
            contentDescription = null
        )

        TopBar(
            isPro = isPro,
            countCoins = countCoins,
            onClick = onClickCountCoins
        )
    }
}

@Composable
private fun FiltersRow(
    title: String,
    templates: List<Template>,
    isInstalledFromRuStore: Boolean?,
    onClickTemplate: (Template) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.tdp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            /*Text(
                text = "Посмотреть все",
                fontSize = 14.tdp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F)
            )*/
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isInstalledFromRuStore == true) {
                item {
                    Spacer(modifier = Modifier.width(10.dp))
                    TemplateFakeItem()
                }
            }
            items(templates) { template ->
                Spacer(modifier = Modifier.width(10.dp))
                TemplateMp4Item(
                    template = template,
                    onClick = { onClickTemplate(template) }
                )
            }
        }
    }
}

@Composable
fun TemplateItem(
    template: Template,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .size(160.dp, 200.dp)
        .clip(RoundedCornerShape(10.dp)),
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = drawableRemote(template.path.substringAfterLast("/")),
            contentScale = ContentScale.Crop,
            alignment = if (template.path.contains("stickers")) Alignment.Center else Alignment.TopCenter,
            contentDescription = null,
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    if (template.placeholder == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = White)
                        }
                    } else {
                        Image(
                            painter = painterResource(resource = template.placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                is AsyncImagePainter.State.Error -> {
                    if (template.placeholder == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(Res.string.error))
                        }
                    } else {
                        Image(
                            painter = painterResource(resource = template.placeholder),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                is AsyncImagePainter.State.Success -> {
                    SubcomposeAsyncImageContent()
                }

                AsyncImagePainter.State.Empty -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                )
                .align(Alignment.BottomCenter),
            text = when (getLanguage()) {
                Language.RU, Language.BE, Language.KK, Language.UK -> template.nameRu
                Language.PT -> template.namePt
                Language.EN -> template.nameEn
            },
            fontSize = 14.tdp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 15.tdp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TemplateMp4Item(
    template: Template,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .width(160.dp)
        .clip(RoundedCornerShape(10.dp)),
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        VideoPlayer(
            modifier = Modifier.size(160.dp),
            templateName = template.path.replace("templates_mp4", "public_mp4"),
            isPlayingGlobal = true,
            onClickVideo = if (getPlatform().name == Platform.Name.IOS) onClick else null,
            onReadyVideo = {}
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                ),
            text = when (getLanguage()) {
                Language.RU, Language.BE, Language.KK, Language.UK -> template.nameRu
                Language.PT -> template.namePt
                Language.EN -> template.nameEn
            },
            fontSize = 14.tdp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 15.tdp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun RowScope.UsersTemplatesMp4Item(
    path: String,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .weight(1F)
        .clip(RoundedCornerShape(10.dp)),
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        VideoPlayer(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1F),
            templateName = path,
            isPlayingGlobal = true,
            onClickVideo = if (getPlatform().name == Platform.Name.IOS) onClick else null,
            onReadyVideo = {}
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                ),
            text = when (getLanguage()) {
                Language.RU, Language.BE, Language.KK, Language.UK -> name
                Language.PT -> name
                Language.EN -> name
            },
            fontSize = 14.tdp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 15.tdp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TemplateFakeItem(
    modifier: Modifier = Modifier
        .width(160.dp)
        .clip(RoundedCornerShape(10.dp)),
) {
    Column(
        modifier = modifier.clickable {
            openUrl(Constants.URL_APP_AVATAR_RU_STORE)
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)),
            painter = painterResource(Res.drawable.ic_launcher_playstore_avatar),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    bottom = 8.dp
                ),
            text = "VerAIArt",
            fontSize = 14.tdp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 15.tdp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun UsersTemplates(
    onClickUserTemplates: (Int) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UsersTemplatesMp4Item(
                path = "public_mp4/Family.mp4",
                name = stringResource(Res.string.photo_by_example),
                onClick = { onClickUserTemplates(0) }
            )

            UsersTemplatesMp4Item(
                path = "public_mp4/Before_and_After.mp4",
                name = stringResource(Res.string.before_and_after_str2),
                onClick = { onClickUserTemplates(1) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UsersTemplatesMp4Item(
                path = "public_mp4/Blogger.mp4",
                name = stringResource(Res.string.video_avatar_for_blog),
                onClick = { onClickUserTemplates(2) }
            )

            UsersTemplatesMp4Item(
                path = "public_mp4/Cats.mp4",
                name = stringResource(Res.string.video_from_prompt_no_photo),
                onClick = { onClickUserTemplates(3) }
            )
        }
    }
}

@Composable
private fun UsersTemplatesItem(
    text: String,
    background: Painter,
    onClick: () -> Unit,
    topText: String? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = WhiteAlpha,
                shape = shape
            )
            .background(
                color = ObsidianInk,
                shape = shape
            )
            .clickable(onClick = onClick)
    ) {
        if (topText != null) {
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = topText,
                    fontSize = 16.tdp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .5F),
                    textAlign = TextAlign.Center
                )

                Image(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    painter = background,
                    contentDescription = null
                )
            }
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = background,
                contentDescription = null
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 10.dp),
            text = text,
            fontSize = 16.tdp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun RequestPermissionNotification() {
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
                when (permissionController.getPermissionState(Permission.REMOTE_NOTIFICATION)) {
                    PermissionState.Granted -> Unit
                    else -> permissionController.providePermission(Permission.REMOTE_NOTIFICATION)
                }
            } catch (e: Exception) {
                getFirebaseKMP().recordException(e)
            }
        }
    }
}

@Preview
@Composable
private fun ListScreenContentPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ListScreenContent(
                isPro = true,
                countCoins = 300,
                templatesMap = emptyMap(),
                isInstalledFromRuStore = true,
                onClickTemplate = {},
                onClickUserTemplate = {},
                onClickCountCoins = {}
            )
        }
    }
}