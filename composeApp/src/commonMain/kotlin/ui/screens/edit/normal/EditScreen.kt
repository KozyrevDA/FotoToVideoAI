package ui.screens.edit.normal

import Platform
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import data.model.FallingStar
import data.model.Template
import dev.icerock.moko.media.compose.BindMediaPickerEffect
import dev.icerock.moko.media.compose.rememberMediaPickerControllerFactory
import dev.icerock.moko.media.compose.toImageBitmap
import dev.icerock.moko.media.picker.MediaSource
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionState.Denied
import dev.icerock.moko.permissions.PermissionState.DeniedAlways
import dev.icerock.moko.permissions.PermissionState.Granted
import dev.icerock.moko.permissions.PermissionState.NotDetermined
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import getImagePicker
import getPlatform
import getSdkVersion
import hideKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.add_person_photo
import phototovideoai.composeapp.generated.resources.add_photo_after
import phototovideoai.composeapp.generated.resources.add_photo_before
import phototovideoai.composeapp.generated.resources.back_button
import phototovideoai.composeapp.generated.resources.coin
import phototovideoai.composeapp.generated.resources.coins_300
import phototovideoai.composeapp.generated.resources.create_for
import phototovideoai.composeapp.generated.resources.ic_add_photo
import phototovideoai.composeapp.generated.resources.onb_back
import phototovideoai.composeapp.generated.resources.photo_animation
import phototovideoai.composeapp.generated.resources.prompt
import phototovideoai.composeapp.generated.resources.trash
import phototovideoai.composeapp.generated.resources.write_your_prompt
import ui.components.GradientToggle
import ui.navigation.AppNavigationActions
import ui.screens.edit.components.ErrorSafetySystemHint
import ui.screens.edit.components.NotEnoughTokensHint
import ui.screens.edit.components.ProgressGeneration
import ui.screens.list.TemplateFakeItem
import ui.screens.list.TemplateMp4Item
import ui.theme.EclipseBlack
import ui.theme.ElectricViolet
import ui.theme.GraphiteSlate
import ui.theme.MidnightShadow
import ui.theme.NeonViolet
import ui.theme.ObsidianInk
import ui.theme.PhotoToVideoAiTheme
import ui.theme.White
import ui.theme.WhiteAlpha
import utils.events.Events
import utils.extensions.ResponsiveText
import utils.extensions.tdp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun EditScreen(
    navigationActions: AppNavigationActions,
    nameFilter: String,
    restore: Boolean,
    activity: Any?,
    viewModel: EditViewModel = koinViewModel(
        parameters = { parametersOf(nameFilter, restore) }
    ),
) {
    val coroutineScope = rememberCoroutineScope()
    val photo1 by viewModel.photo1.collectAsState(Dispatchers.Main)
    val photo2 by viewModel.photo2.collectAsState(Dispatchers.Main)
    val waitResult by viewModel.waitResult.collectAsState(Dispatchers.Main)
    val notEnoughCoins by viewModel.notEnoughCoins.collectAsState(Dispatchers.Main)
    val errorSafetySystem by viewModel.errorSafetySystem.collectAsState(Dispatchers.Main)
    val userPrompt by viewModel.prompt.collectAsState(Dispatchers.Main)
    val clickPickImagePhoto1 = remember { mutableStateOf(false) }
    val clickPickImagePhoto2 = remember { mutableStateOf(false) }
    val templatesMapState = viewModel.templatesMap.collectAsState(Dispatchers.Main)
    var writeStoragePermission by remember { mutableStateOf(false) }
    var isButtonEnabled by remember { mutableStateOf(true) }
    var currentTemplatePath by remember { mutableStateOf<String?>(null) }
    val isOnlyPrompt = nameFilter == "videoFromPrompt"
    val checkedPromptState = remember {
        mutableStateOf(!userPrompt.isNullOrBlank() || isOnlyPrompt)
    }
    val isVisibleTemplatesRow = !listOf(
        "animatePhoto",
        "beforeAfter",
        "videoAvatar",
        "videoFromPrompt"
    ).contains(nameFilter)

    LaunchedEffect(Unit) {
        currentTemplatePath = nameFilter
    }

    LifecycleResumeEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.updateGenerateQueue()
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.generatedIdVideoEvent.collect { idVideo ->
            if (!notEnoughCoins && !errorSafetySystem) {
                navigationActions.navigateToResultVideoScreen(idVideo = idVideo)
            }
        }
    }

    EditScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MidnightShadow),
        photo1 = photo1,
        photo2 = photo2,
        userPrompt = userPrompt,
        waitResult = waitResult,
        templatesMap = templatesMapState.value,
        currentTemplatePath = currentTemplatePath,
        clickPickImagePhoto1 = clickPickImagePhoto1,
        clickPickImagePhoto2 = clickPickImagePhoto2,
        isBeforeAndAfter = nameFilter == "beforeAfter",
        isVisibleTemplatesRow = isVisibleTemplatesRow,
        isOnlyPrompt = isOnlyPrompt,
        checkedPromptState = checkedPromptState,
        isInstalledFromRuStore = viewModel.isInstalledFromRuStore,
        onPickPhoto1 = { viewModel.savePhoto1(it) },
        onPickPhoto2 = { viewModel.savePhoto2(it) },
        onClickGen = {
            if (!isButtonEnabled) return@EditScreenContent

            coroutineScope.launch {
                isButtonEnabled = false
                if (writeStoragePermission) {
                    if (viewModel.isPastAuth) {
                        hideKeyboard(activity)
                        delay(200L)
                        viewModel.updateGenerateQueue(true)
                        viewModel.generateVideo(currentTemplatePath = currentTemplatePath)
                        Events.put(Events.SCREEN_4_FILTER_GEN)
                    } else {
                        navigationActions.navigateToAuthScreen()
                    }
                }
                isButtonEnabled = true
            }
        },
        onClickBack = {
            navigationActions.back()
            Events.put(Events.SCREEN_4_FILTER_BACK)
        },
        onClickTemplate = {
            currentTemplatePath = if (currentTemplatePath == it) null else it
        },
        onChangePrompt = {
            viewModel.savePrompt(it)
        }
    )

    val sdkAndroid = getSdkVersion()?.number
    if (sdkAndroid != null && sdkAndroid <= 28) {
        WriteStoragePermission {
            writeStoragePermission = true
        }
    } else {
        writeStoragePermission = true
    }

    if (getPlatform().name == Platform.Name.IOS) {
        GalleryPermission(
            clickPickImage = clickPickImagePhoto1,
            onPickImage = viewModel::savePhoto1
        )

        GalleryPermission(
            clickPickImage = clickPickImagePhoto2,
            onPickImage = viewModel::savePhoto2
        )
    }

    if (notEnoughCoins) {
        NotEnoughTokensHint(
            onClickBuy = {
                navigationActions.navigateToPaywallScreen(prevScreen = "экран_редактирования")
            },
            onClickBack = { viewModel.hideNotEnoughCoins() }
        )
    }

    if (errorSafetySystem) {
        ErrorSafetySystemHint(onClickClose = { viewModel.hideErrorSafetySystem() })
    }
}

@Composable
private fun EditScreenContent(
    photo1: ImageBitmap?,
    photo2: ImageBitmap?,
    userPrompt: String?,
    waitResult: Boolean,
    templatesMap: Map<String, List<Template>>,
    currentTemplatePath: String?,
    clickPickImagePhoto1: MutableState<Boolean>,
    clickPickImagePhoto2: MutableState<Boolean>,
    isBeforeAndAfter: Boolean,
    isVisibleTemplatesRow: Boolean,
    isOnlyPrompt: Boolean,
    checkedPromptState: MutableState<Boolean>,
    isInstalledFromRuStore: Boolean?,
    onClickBack: () -> Unit,
    onPickPhoto1: (ImageBitmap?) -> Unit,
    onPickPhoto2: (ImageBitmap?) -> Unit,
    onClickGen: () -> Unit,
    onClickTemplate: (String) -> Unit,
    onChangePrompt: (String) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(color = MidnightShadow),
) {
    val density = LocalDensity.current
    var paddingBottom by remember { mutableStateOf(0.dp) }

    Box(
        modifier = modifier.fillMaxSize(),
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
                .padding(bottom = paddingBottom),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Top(onClickBack = onClickBack)

            Spacer(modifier = Modifier.height(12.dp))

            if (!isOnlyPrompt) {
                if (isBeforeAndAfter) {
                    DoubleImagePicker(
                        photo1 = photo1,
                        photo2 = photo2,
                        waitResult = waitResult,
                        clickPickImagePhoto1 = clickPickImagePhoto1,
                        clickPickImagePhoto2 = clickPickImagePhoto2,
                        onPickPhoto1 = onPickPhoto1,
                        onPickPhoto2 = onPickPhoto2

                    )
                } else {
                    ImagePicker(
                        avatar = photo1,
                        waitResult = waitResult,
                        clickPickImage = clickPickImagePhoto1,
                        onPickImage = onPickPhoto1
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (!waitResult) {
                if (isVisibleTemplatesRow) {
                    TemplatesRow(
                        templatesMap = templatesMap,
                        currentTemplatePath = currentTemplatePath,
                        isInstalledFromRuStore = isInstalledFromRuStore,
                        onClickTemplate = onClickTemplate
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                PromptPanel(
                    userPrompt = userPrompt,
                    checkedPromptState = checkedPromptState,
                    onChangePrompt = onChangePrompt
                )
            } else {
                ProgressGeneration(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        if (!waitResult) {
            Column(
                modifier = Modifier
                    .padding(bottom = 50.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(color = MidnightShadow)
                    .onGloballyPositioned { coordinates ->
                        if (paddingBottom == 0.dp) {
                            paddingBottom = with(density) { coordinates.size.height.toDp() + 60.dp }
                        }
                    }
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Button(
                    modifier = Modifier.height(52.dp),
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
                        Text(
                            text = stringResource(Res.string.create_for),
                            fontSize = 18.tdp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.coin),
                                contentDescription = null
                            )

                            Text(
                                text = stringResource(Res.string.coins_300),
                                fontSize = 18.tdp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.ImagePicker(
    avatar: ImageBitmap?,
    waitResult: Boolean,
    clickPickImage: MutableState<Boolean>,
    onPickImage: (ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier
        .weight(1F, false)
        .padding(horizontal = 16.dp),
) {
    val shape = RoundedCornerShape(8.dp)
    val picker = getImagePicker()
    val showPicker = remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        showPicker.value = false

        onPauseOrDispose {}
    }

    picker?.PickImageFromGallery(
        showPicker = showPicker,
        onPicked = { image -> onPickImage(image) }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(shape = shape)
                .run {
                    if (waitResult) {
                        blur(16.dp)
                    } else {
                        this
                    }
                }
                .clickable(
                    enabled = !waitResult,
                    onClick = {
                        when (getPlatform().name) {
                            Platform.Name.ANDROID -> showPicker.value = true && !waitResult
                            Platform.Name.IOS -> clickPickImage.value = true
                        }
                        Events.put(Events.SCREEN_4_FILTER_PICK_IMAGE)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F / .5F)
                    .run {
                        if (avatar == null) {
                            background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        EclipseBlack,
                                        GraphiteSlate,
                                        EclipseBlack
                                    )
                                )
                            )
                        } else {
                            background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        EclipseBlack,
                                        White,
                                    )
                                )
                            )
                        }
                    }
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                if (avatar == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(26.dp),
                            imageVector = vectorResource(Res.drawable.ic_add_photo),
                            colorFilter = ColorFilter.tint(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            contentDescription = null
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth(.9F),
                            text = stringResource(Res.string.add_person_photo),
                            fontSize = 14.tdp,
                            lineHeight = 14.tdp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = avatar,
                        contentDescription = null
                    )

                    IconButton(
                        modifier = Modifier
                            .size(42.dp)
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 10.dp),
                        onClick = { onPickImage(null) }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(Res.drawable.trash),
                            contentDescription = null
                        )
                    }
                }
            }
        }

        if (waitResult) {
            FallingStarsField(modifier = Modifier.fillMaxSize(.2F))
        }
    }
}

@Composable
private fun DoubleImagePicker(
    photo1: ImageBitmap?,
    photo2: ImageBitmap?,
    waitResult: Boolean,
    clickPickImagePhoto1: MutableState<Boolean>,
    clickPickImagePhoto2: MutableState<Boolean>,
    onPickPhoto1: (ImageBitmap?) -> Unit,
    onPickPhoto2: (ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    val shape = RoundedCornerShape(12.dp)
    val pickerPhoto1 = getImagePicker()
    val pickerPhoto2 = getImagePicker()
    val showPickerPhoto1 = remember { mutableStateOf(false) }
    val showPickerPhoto2 = remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        showPickerPhoto1.value = false
        showPickerPhoto2.value = false

        onPauseOrDispose {}
    }

    pickerPhoto1?.PickImageFromGallery(
        showPicker = showPickerPhoto1,
        onPicked = { image -> onPickPhoto1(image) }
    )
    pickerPhoto2?.PickImageFromGallery(
        showPicker = showPickerPhoto2,
        onPicked = { image -> onPickPhoto2(image) }
    )


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .run {
                    if (waitResult) {
                        blur(16.dp)
                    } else {
                        this
                    }
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .aspectRatio(1F),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp, end = 8.dp)
                            .clip(shape)
                            .border(
                                width = 1.dp,
                                color = WhiteAlpha,
                                shape = shape
                            )
                            .run {
                                if (photo1 == null) {
                                    background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                EclipseBlack,
                                                GraphiteSlate,
                                                EclipseBlack
                                            )
                                        )
                                    )
                                } else {
                                    background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                EclipseBlack,
                                                White,
                                            )
                                        )
                                    )
                                }
                            }
                            .clickable {
                                when (getPlatform().name) {
                                    Platform.Name.ANDROID -> showPickerPhoto1.value = true
                                    Platform.Name.IOS -> clickPickImagePhoto1.value = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photo1 == null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 20.dp,
                                    alignment = Alignment.CenterVertically
                                )
                            ) {
                                Image(
                                    imageVector = vectorResource(Res.drawable.ic_add_photo),
                                    contentDescription = null
                                )
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(Res.string.add_photo_before),
                                    fontSize = 16.tdp,
                                    lineHeight = 17.tdp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Image(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp),
                                bitmap = photo1,
                                contentDescription = null
                            )

                            IconButton(
                                modifier = Modifier
                                    .size(42.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 10.dp, end = 10.dp),
                                onClick = { onPickPhoto1(null) }
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(Res.drawable.trash),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1F)
                        .aspectRatio(1F),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp, end = 8.dp)
                            .clip(shape)
                            .border(
                                width = 1.dp,
                                color = WhiteAlpha,
                                shape = RoundedCornerShape(12.dp)
                            ).run {
                                if (photo2 == null) {
                                    background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                EclipseBlack,
                                                GraphiteSlate,
                                                EclipseBlack
                                            )
                                        )
                                    )
                                } else {
                                    background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                EclipseBlack,
                                                White,
                                            )
                                        )
                                    )
                                }
                            }
                            .clickable {
                                when (getPlatform().name) {
                                    Platform.Name.ANDROID -> showPickerPhoto2.value = true
                                    Platform.Name.IOS -> clickPickImagePhoto2.value = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photo2 == null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    space = 20.dp,
                                    alignment = Alignment.CenterVertically
                                )
                            ) {
                                Image(
                                    imageVector = vectorResource(Res.drawable.ic_add_photo),
                                    contentDescription = null
                                )
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(Res.string.add_photo_after),
                                    fontSize = 16.tdp,
                                    lineHeight = 17.tdp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Image(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp),
                                bitmap = photo2,
                                contentDescription = null
                            )

                            IconButton(
                                modifier = Modifier
                                    .size(42.dp)
                                    .align(Alignment.TopEnd)
                                    .padding(top = 10.dp, end = 10.dp),
                                onClick = { onPickPhoto2(null) }
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painterResource(Res.drawable.trash),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            if (waitResult) {
                FallingStarsField(modifier = Modifier.fillMaxSize(.2F))
            }
        }
    }
}

@Composable
fun WriteStoragePermission(onPermissionGranted: () -> Unit) {
    val mediaFactory = rememberMediaPickerControllerFactory()
    val mediaPicker = remember(mediaFactory) { mediaFactory.createMediaPickerController() }
    val permissionFactory = rememberPermissionsControllerFactory()
    val permissionController = remember(permissionFactory) {
        permissionFactory.createPermissionsController()
    }
    var checkPermissions by remember { mutableStateOf(false) }

    BindEffect(permissionController)
    BindMediaPickerEffect(mediaPicker)

    LaunchedEffect(checkPermissions) {
        if (checkPermissions) {
            repeat(240) {
                delay(500L)
                when (permissionController.getPermissionState(Permission.WRITE_STORAGE)) {
                    PermissionState.Granted -> {
                        onPermissionGranted()
                        checkPermissions = false
                    }

                    else -> {}
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        when (permissionController.getPermissionState(Permission.WRITE_STORAGE)) {
            PermissionState.NotDetermined,
            PermissionState.Denied,
            PermissionState.DeniedAlways,
                -> {
                checkPermissions = true
                permissionController.providePermission(Permission.WRITE_STORAGE)
            }

            PermissionState.Granted -> {
                onPermissionGranted()
            }
        }
    }
}

@Composable
fun GalleryPermission(
    clickPickImage: MutableState<Boolean>,
    onPickImage: (ImageBitmap) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val mediaFactory = rememberMediaPickerControllerFactory()
    val mediaPicker = remember(mediaFactory) { mediaFactory.createMediaPickerController() }
    val permissionFactory = rememberPermissionsControllerFactory()
    val permissionController = remember(permissionFactory) {
        permissionFactory.createPermissionsController()
    }
    var checkPermissions by remember { mutableStateOf(false) }

    BindEffect(permissionController)
    BindMediaPickerEffect(mediaPicker)

    fun onPickOpen() {
        coroutineScope.launch {
            when (permissionController.getPermissionState(Permission.GALLERY)) {
                NotDetermined -> {
                    checkPermissions = true
                    permissionController.providePermission(Permission.GALLERY)
                }

                Granted -> {
                    runCatching {
                        onPickImage?.invoke(
                            mediaPicker.pickImage(MediaSource.GALLERY)
                                .toImageBitmap()
                        )
                    }
                }

                Denied -> {
                    checkPermissions = true
                    permissionController.providePermission(Permission.GALLERY)
                }

                DeniedAlways -> {
                    checkPermissions = true
                    permissionController.providePermission(Permission.GALLERY)
                }
            }
        }
    }

    LaunchedEffect(checkPermissions) {
        if (checkPermissions) {
            repeat(240) {
                delay(500L)
                when (permissionController.getPermissionState(Permission.GALLERY)) {
                    Granted -> {
                        runCatching {
                            onPickImage?.invoke(
                                mediaPicker.pickImage(MediaSource.GALLERY)
                                    .toImageBitmap()
                            )
                        }

                        checkPermissions = false
                    }

                    else -> {}
                }
            }
        }
    }

    LaunchedEffect(clickPickImage.value) {
        if (clickPickImage.value) {
            clickPickImage.value = false
            onPickOpen()
        }
    }
}

@Composable
private fun Top(
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(top = 60.dp),
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.CenterStart),
            onClick = onClickBack
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.back_button),
                contentDescription = null
            )
        }

        ResponsiveText(
            text = stringResource(Res.string.photo_animation),
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
                fontSize = 20.tdp,
                fontWeight = FontWeight.Bold
            ),
        )
    }
}

@Composable
fun FallingStarsField(
    starsCount: Int = 10,
    minSize: Float = 12F,
    maxSize: Float = 40F,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val stars = remember {
            List(starsCount) {
                FallingStar(
                    x = Random.nextFloat() * widthPx,
                    y = -Random.nextFloat() * heightPx,
                    size = Random.nextFloat() * (maxSize - minSize) + minSize,
                    speed = Random.nextFloat() * 100F + 50F,
                    phase = Random.nextFloat() * 6.28F,
                    rotation = Random.nextFloat() * 360F
                )
            }.toMutableStateList()
        }

        LaunchedEffect(Unit) {
            val frameTime = 16L
            while (true) {
                withFrameNanos { delta ->
                    val dt = frameTime / 1000F
                    stars.forEachIndexed { i, star ->
                        var newY = star.y + star.speed * dt
                        if (newY > heightPx + star.size) {
                            newY = -star.size
                            stars[i] = star.copy(
                                x = Random.nextFloat() * widthPx,
                                y = newY,
                                size = Random.nextFloat() * (maxSize - minSize) + minSize,
                                speed = Random.nextFloat() * 100F + 50F,
                                phase = Random.nextFloat() * 6.28F
                            )
                        } else {
                            stars[i] = star.copy(y = newY)
                        }
                    }
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                val swayX = sin(star.y / 50F + star.phase) * 10F
                rotate(
                    degrees = star.rotation,
                    pivot = Offset(star.x + swayX, star.y)
                ) {
                    drawStar(
                        center = Offset(star.x + swayX, star.y),
                        spikes = 8,
                        outerRadius = star.size,
                        innerRadius = star.size / 3,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStar(
    center: Offset,
    spikes: Int,
    outerRadius: Float,
    innerRadius: Float,
    color: Color,
) {
    val path = Path()
    var rot = PI / 2 * 3
    val step = PI / spikes

    path.moveTo(center.x, center.y - outerRadius)

    for (i in 0 until spikes) {
        var x = center.x + cos(rot).toFloat() * outerRadius
        var y = center.y + sin(rot).toFloat() * outerRadius
        path.lineTo(x, y)
        rot += step

        x = center.x + cos(rot).toFloat() * innerRadius
        y = center.y + sin(rot).toFloat() * innerRadius
        path.lineTo(x, y)
        rot += step
    }

    path.close()
    drawPath(path, color)
}

@Composable
private fun PromptPanel(
    checkedPromptState: MutableState<Boolean>,
    userPrompt: String?,
    onChangePrompt: (String) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    val shape = RoundedCornerShape(30.dp)
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .background(
                color = EclipseBlack,
                shape = shape
            )
            .border(
                width = .9.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .1F),
                shape = shape
            )
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.prompt),
                fontSize = 16.tdp,
                lineHeight = 16.tdp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            GradientToggle(
                checked = checkedPromptState.value,
                onCheckedChange = { checkedPromptState.value = it }
            )
        }

        if (checkedPromptState.value) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 200.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .clip(shape = RoundedCornerShape(18.dp))
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused }
                    .then(
                        if (isFocused) {
                            Modifier.border(
                                width = 0.8.dp,
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        NeonViolet,
                                        ElectricViolet,
                                        NeonViolet,
                                    )
                                ),
                                shape = shape
                            )
                        } else {
                            Modifier
                        }
                    ),
                value = userPrompt ?: "",
                singleLine = false,
                maxLines = 6,
                placeholder = {
                    Text(
                        stringResource(Res.string.write_your_prompt),
                        style = TextStyle(
                            fontSize = 16.tdp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5F)
                        )
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.tdp,
                    textAlign = TextAlign.Start
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ObsidianInk,
                    unfocusedContainerColor = ObsidianInk,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                onValueChange = {
                    if (it.length <= 300) {
                        onChangePrompt(it)
                    }
                }
            )
        }
    }
}

@Composable
private fun ColumnScope.TemplatesRow(
    templatesMap: Map<String, List<Template>>,
    currentTemplatePath: String?,
    isInstalledFromRuStore: Boolean?,
    onClickTemplate: (String) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .weight(1F, false),
) {
    val listState = rememberLazyListState()
    val templatesList = remember(templatesMap) {
        templatesMap.values.flatten()
    }

    LaunchedEffect(currentTemplatePath, templatesList) {
        val index = templatesList.indexOfFirst {
            currentTemplatePath?.let { other -> it.path.contains(other) } == true
        }
        if (index != -1) {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top
    ) {
        val shape = RoundedCornerShape(12.dp)

        if (isInstalledFromRuStore == true) {
            item {
                TemplateFakeItem()
            }
        }

        items(
            items = templatesList,
            key = { it.path }
        ) { template ->
            Box(
                modifier = Modifier
                    .run {
                        if (
                            currentTemplatePath != null &&
                            template.path.contains(currentTemplatePath)
                        ) {
                            background(
                                brush = Brush.sweepGradient(
                                    listOf(
                                        NeonViolet,
                                        ElectricViolet,
                                        NeonViolet,
                                    )
                                ),
                                shape = shape
                            )
                        } else this
                    }
                    .clip(shape)
            ) {
                TemplateMp4Item(
                    template = template,
                    onClick = { onClickTemplate(template.path) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun TemplatesRowPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                TemplatesRow(
                    templatesMap = emptyMap(),
                    currentTemplatePath = null,
                    isInstalledFromRuStore = true,
                    onClickTemplate = {}
                )
            }
        }
    }
}

@Preview
@Composable
private fun PromptPanelPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PromptPanel(
                userPrompt = "",
                checkedPromptState = mutableStateOf(false),
                onChangePrompt = {}
            )
        }
    }
}

@Preview
@Composable
private fun EditScreenContentPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditScreenContent(
                photo1 = null,
                photo2 = null,
                userPrompt = "",
                waitResult = false,
                templatesMap = emptyMap(),
                currentTemplatePath = null,
                clickPickImagePhoto1 = mutableStateOf(false),
                clickPickImagePhoto2 = mutableStateOf(false),
                isBeforeAndAfter = false,
                isOnlyPrompt = true,
                checkedPromptState = mutableStateOf(true),
                isInstalledFromRuStore = true,
                isVisibleTemplatesRow = true,
                onClickBack = {},
                onPickPhoto1 = {},
                onPickPhoto2 = {},
                onClickTemplate = {},
                onClickGen = {},
                onChangePrompt = {}
            )
        }
    }
}