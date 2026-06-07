package ui.screens.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.model.PageStandard
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.added_text_promt
import phototovideoai.composeapp.generated.resources.ai_photo_intro
import phototovideoai.composeapp.generated.resources.before_and_after
import phototovideoai.composeapp.generated.resources.change_music
import phototovideoai.composeapp.generated.resources.compare_result
import phototovideoai.composeapp.generated.resources.done_templates
import phototovideoai.composeapp.generated.resources.next
import phototovideoai.composeapp.generated.resources.onb_1
import phototovideoai.composeapp.generated.resources.onb_2
import phototovideoai.composeapp.generated.resources.onb_2_back
import phototovideoai.composeapp.generated.resources.onb_3
import phototovideoai.composeapp.generated.resources.onb_3_back
import phototovideoai.composeapp.generated.resources.onb_4
import phototovideoai.composeapp.generated.resources.onb_4_back
import phototovideoai.composeapp.generated.resources.onb_5
import phototovideoai.composeapp.generated.resources.onb_5_back
import phototovideoai.composeapp.generated.resources.onb_6
import phototovideoai.composeapp.generated.resources.onb_6_back
import phototovideoai.composeapp.generated.resources.photos_to_life
import phototovideoai.composeapp.generated.resources.record_voice
import phototovideoai.composeapp.generated.resources.replace_music
import phototovideoai.composeapp.generated.resources.templates_styles
import phototovideoai.composeapp.generated.resources.voice_acting
import phototovideoai.composeapp.generated.resources.with_promt_or_not
import ui.components.Segment
import ui.navigation.AppNavigationActions
import ui.screens.onboarding.pages.Page1
import ui.screens.onboarding.pages.Page3
import ui.screens.onboarding.pages.VideoPage1
import ui.screens.onboarding.pages.VideoPage2
import ui.screens.onboarding.pages.VideoPage4
import ui.screens.onboarding.pages.VideoPage5
import ui.screens.onboarding.pages.VideoPage6
import ui.theme.ElectricViolet
import ui.theme.GraphiteGray
import ui.theme.MidnightShadow
import ui.theme.NeonViolet
import ui.theme.PhotoToVideoAiTheme
import ui.theme.TranslucentWhite
import utils.events.Events

private const val MAX_PAGES = 6

@Composable
fun OnboardingScreen(
    navigationActions: AppNavigationActions,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    OnboardingScreenContent(
        onClickToNext = {
            coroutineScope.launch {
                viewModel.setPastOnboarding()
                val showTrial = viewModel.getShowTrialGeneration()
                if (showTrial) {
                    navigationActions.navigateToTrialGenerationScreen(popUpTo = true)
                } else {
                    navigationActions.navigateToListScreen(popUpTo = true)
                    if (viewModel.getShowStartPaywall()) {
                        navigationActions.navigateToPaywallScreen(prevScreen = "экран_список_фильтров")
                    }
                }
            }
        }
    )
}

@Composable
private fun OnboardingScreenContent(
    initNumberPage: Int = 0,
    onClickToNext: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(color = MidnightShadow),
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val timePerSegment = 8_000
    val pagerState = rememberPagerState(
        pageCount = { MAX_PAGES },
        initialPage = initNumberPage
    )
    val pages = createPagesList()
    var startReels by remember { mutableStateOf(false) }
    val currentNumberPage = pagerState.currentPage % pages.size + 1
    var pageBottomPadding by remember { mutableStateOf(0.dp) }

    LaunchedEffect(startReels, currentNumberPage) {
        if (!startReels) return@LaunchedEffect

        pages.forEach { it.segment.reset() }
        pages[currentNumberPage - 1].segment.start()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                startReels = true
            },
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = pageBottomPadding),
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            val pageEntity = pages[page % pages.size]

            when (page) {
                0 -> VideoPage1(
                    onbBackImage = pageEntity.onbBackImage,
                    onbImage = pageEntity.onbImage,
                    title = pageEntity.title,
                    desc1 = pageEntity.desc1,
                    pageNumber = page
                )

                1 -> VideoPage2(
                    onbBackImage = pageEntity.onbBackImage,
                    onbImage = pageEntity.onbImage,
                    title = pageEntity.title,
                    desc1 = pageEntity.desc1,
                    pageNumber = page
                )

                2 -> Page3(
                    onbBackImage = pageEntity.onbBackImage,
                    onbImage = pageEntity.onbImage,
                    title = pageEntity.title,
                    desc1 = pageEntity.desc1,
                    pageNumber = page
                )

                3 -> VideoPage4(
                    onbBackImage = pageEntity.onbBackImage,
                    onbImage = pageEntity.onbImage,
                    title = pageEntity.title,
                    desc1 = pageEntity.desc1,
                    pageNumber = page
                )

                4 -> VideoPage5(
                    onbBackImage = pageEntity.onbBackImage,
                    onbImage = pageEntity.onbImage,
                    title = pageEntity.title,
                    desc1 = pageEntity.desc1,
                    pageNumber = page
                )

                5 -> VideoPage6(
                    onbBackImage = pageEntity.onbBackImage,
                    onbImage = pageEntity.onbImage,
                    title = pageEntity.title,
                    desc1 = pageEntity.desc1,
                    pageNumber = page
                )

                else -> Page1(
                    onbBackImage = pageEntity.onbBackImage,
                    onbImage = pageEntity.onbImage,
                    title = pageEntity.title,
                    desc1 = pageEntity.desc1,
                    pageNumber = page
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .onGloballyPositioned { coord ->
                    pageBottomPadding = with(density) { coord.size.height.toDp() }
                }
        ) {
            ProgressPages(
                modifier = Modifier.padding(top = 6.dp),
                countPages = pages.size,
                currentPage = pagerState.currentPage,
                timePerSegment = timePerSegment,
                segments = pages.map { it.segment },
                onTimerEnd = {
                    if (pagerState.currentPage + 1 >= MAX_PAGES) {
                        onClickToNext()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MidnightShadow
                ),
                onClick = {
                    if (pagerState.currentPage + 1 >= MAX_PAGES) {
                        onClickToNext()
                        Events.put(Events.SCREEN_1_TO_AUTH)
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                        }
                        Events.put(Events.SCREEN_1_NEXT)
                    }
                }
            ) {
                Text(text = stringResource(Res.string.next))
            }

            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

@Composable
private fun ProgressPages(
    modifier: Modifier = Modifier,
    countPages: Int,
    currentPage: Int,
    timePerSegment: Int,
    segments: List<Segment>,
    onTimerEnd: () -> Unit,
) {
    val spacer = remember { 8.dp }
    var widthSegmentInPx by remember { mutableIntStateOf(0) }
    val widthSegmentDp =
        (with(LocalDensity.current) { widthSegmentInPx.toDp() } - spacer * countPages) / countPages

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = GraphiteGray.copy(alpha = .2F),
                shape = RoundedCornerShape(20.dp)
            )
            .onGloballyPositioned { coordinates ->
                widthSegmentInPx = coordinates.size.width
            },
        horizontalArrangement = Arrangement.Center
    ) {
        itemsIndexed(segments) { index, segment ->
            key(currentPage) {
                ProgressPageSegment(
                    index = index,
                    currentPage = currentPage,
                    progress = segment.progress.floatValue,
                    widthSegmentDp = widthSegmentDp,
                    timePerSegment = timePerSegment,
                    onTimerEnd = onTimerEnd
                )

                if (countPages > 1 && index < countPages - 1)
                    Spacer(modifier = Modifier.width(spacer))
            }
        }
    }
}

@Composable
private fun ProgressPageSegment(
    index: Int,
    currentPage: Int,
    progress: Float,
    widthSegmentDp: Dp,
    timePerSegment: Int,
    onTimerEnd: () -> Unit,
) {
    val shape = remember { RoundedCornerShape(2.dp) }
    val targetProgress = when {
        currentPage == 0 -> 0F
        index < currentPage -> 1F
        index == currentPage -> progress
        else -> 0F
    }

    val animProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = when {
            currentPage == 0 -> snap()
            index < currentPage -> snap()
            index == currentPage -> tween(
                durationMillis = timePerSegment,
                easing = LinearEasing
            )

            else -> snap()
        },
        label = "animProgress"
    )

    LaunchedEffect(animProgress) {
        if (index == currentPage && animProgress == 1F) {
            onTimerEnd()
        }
    }

    GradientLinearProgress(
        modifier = Modifier
            .width(widthSegmentDp)
            .padding(vertical = 4.dp)
            .height(10.dp)
            .clip(shape),
        progress = animProgress,
        trackColor = GraphiteGray.copy(alpha = .26F),
        gradient = Brush.horizontalGradient(
            colors = listOf(
                ElectricViolet,
                NeonViolet
            )
        )
    )
}

@Composable
private fun createPage(
    number: Int,
    onbBackImage: Painter,
    onbImage: Painter,
    title: String,
    desc1: String,
) = PageStandard(
    number = number,
    onbBackImage = onbBackImage,
    onbImage = onbImage,
    title = title,
    desc1 = desc1,
)

@Composable
private fun createPagesList(): List<PageStandard> {
    val pageEntities = mutableListOf<PageStandard>()

    pageEntities.add(
        createPage(
            number = 1,
            onbBackImage = painterResource(Res.drawable.onb_2_back),
            onbImage = painterResource(Res.drawable.onb_1),
            title = stringResource(Res.string.photos_to_life),
            desc1 = stringResource(Res.string.ai_photo_intro),
        )
    )

    pageEntities.add(
        createPage(
            number = 2,
            onbBackImage = painterResource(Res.drawable.onb_2_back),
            onbImage = painterResource(Res.drawable.onb_2),
            title = stringResource(Res.string.with_promt_or_not),
            desc1 = stringResource(Res.string.added_text_promt),
        )
    )

    pageEntities.add(
        createPage(
            number = 3,
            onbBackImage = painterResource(Res.drawable.onb_3_back),
            onbImage = painterResource(Res.drawable.onb_3),
            title = stringResource(Res.string.replace_music),
            desc1 = stringResource(Res.string.change_music),
        )
    )

    pageEntities.add(
        createPage(
            number = 4,
            onbBackImage = painterResource(Res.drawable.onb_4_back),
            onbImage = painterResource(Res.drawable.onb_4),
            title = stringResource(Res.string.done_templates),
            desc1 = stringResource(Res.string.templates_styles),
        )
    )

    pageEntities.add(
        createPage(
            number = 5,
            onbBackImage = painterResource(Res.drawable.onb_5_back),
            onbImage = painterResource(Res.drawable.onb_5),
            title = stringResource(Res.string.before_and_after),
            desc1 = stringResource(Res.string.compare_result),
        )
    )

    pageEntities.add(
        createPage(
            number = 6,
            onbBackImage = painterResource(Res.drawable.onb_6_back),
            onbImage = painterResource(Res.drawable.onb_6),
            title = stringResource(Res.string.voice_acting),
            desc1 = stringResource(Res.string.record_voice),
        )
    )

    return pageEntities
}

@Composable
private fun GradientLinearProgress(
    progress: Float,
    gradient: Brush,
    shape: Shape = RoundedCornerShape(20.dp),
    trackColor: Color = TranslucentWhite,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.clip(shape)) {
        val width = size.width
        val progressWidth = width * progress.coerceIn(0F, 1F)

        drawRect(
            color = trackColor,
            size = Size(width, size.height)
        )

        drawRect(
            brush = gradient,
            size = Size(progressWidth, size.height)
        )
    }
}

@Preview
@Composable
private fun OnboardingScreenContentPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            OnboardingScreenContent(
                initNumberPage = 6,
                onClickToNext = {}
            )
        }
    }
}