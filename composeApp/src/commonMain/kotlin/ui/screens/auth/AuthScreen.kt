package ui.screens.auth

import Platform
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.Constants.URL_PRIVACY_POLICY
import app.Constants.URL_TERMS_OF_USE
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.agree_text
import phototovideoai.composeapp.generated.resources.and_text
import phototovideoai.composeapp.generated.resources.appleid_button
import phototovideoai.composeapp.generated.resources.auth_1
import phototovideoai.composeapp.generated.resources.continue_apple_id
import phototovideoai.composeapp.generated.resources.continue_google
import phototovideoai.composeapp.generated.resources.continue_vk
import phototovideoai.composeapp.generated.resources.ic_google
import phototovideoai.composeapp.generated.resources.ic_vk
import phototovideoai.composeapp.generated.resources.login_prompt
import phototovideoai.composeapp.generated.resources.privacy_policy
import phototovideoai.composeapp.generated.resources.terms_of_use
import phototovideoai.composeapp.generated.resources.welcome
import data.model.AuthResponse
import getPlatform
import getProviderAuthPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import openUrl
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.navigation.AppNavigationActions
import ui.theme.ElectricViolet
import ui.theme.MidnightShadow
import ui.theme.PhotoToVideoAiTheme
import utils.events.Events
import utils.extensions.tdp

@Composable
fun AuthScreen(
    navigationActions: AppNavigationActions,
    onAuthVK: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val authResponseState by viewModel.authResponseFlow.collectAsState(Dispatchers.Main)
    val isLoading by viewModel.isLoading.collectAsState(Dispatchers.Main)
    var isBackNav by remember { mutableStateOf(false) }

    LaunchedEffect(authResponseState) {
        when (authResponseState) {
            is AuthResponse.Authorized -> {
                if (!viewModel.isNotFirstOpenApp) {
                    viewModel.setPassedAuth()
                    navigationActions.navigateToListScreen(popUpTo = true)
                    if (viewModel.getShowStartPaywall()) {
                        navigationActions.navigateToPaywallScreen(prevScreen = "экран_список_фильтров")
                    }
                } else if (!isBackNav) {
                    isBackNav = true
                    viewModel.setPassedAuth()
                    navigationActions.back()
                }
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AuthScreenContent(
            onClickPrivacyPolicy = { openUrl(URL_PRIVACY_POLICY) },
            onClickTermsOfUse = { openUrl(URL_TERMS_OF_USE) },
            onClickAuthVk = {
                viewModel.applyLoading()
                onAuthVK()
                Events.put(Events.SCREEN_2_AUTH_VK)
            },
            onClickAuthGoogle = {
                viewModel.applyLoading()
                viewModel.authGoogleProvider()
                getProviderAuthPage().launchAuthGoogle()
                Events.put(Events.SCREEN_2_AUTH_GOOGLE)
            },
            onClickAuthApple = {
                coroutineScope.launch {
                    val signInCredential = getProviderAuthPage().launchAuthApple()
                    viewModel.authApple(signInCredential)
                    Events.put(Events.SCREEN_2_AUTH_APPLE)
                }
            },
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MidnightShadow)
                    .blur(2.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
private fun AuthScreenContent(
    onClickPrivacyPolicy: () -> Unit,
    onClickTermsOfUse: () -> Unit,
    onClickAuthVk: () -> Unit,
    onClickAuthGoogle: () -> Unit,
    onClickAuthApple: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(color = MidnightShadow),
) {
    val platformName = getPlatform().name

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F, false),
            painter = painterResource(Res.drawable.auth_1),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Text(
            modifier = Modifier.fillMaxWidth(.9F),
            text = stringResource(Res.string.welcome),
            fontSize = 30.tdp,
            lineHeight = 33.tdp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(.9F),
            text = stringResource(Res.string.login_prompt),
            fontSize = 14.tdp,
            lineHeight = 17.tdp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(.9F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            if (platformName == Platform.Name.ANDROID) {
                AuthButton(
                    text = stringResource(Res.string.continue_vk),
                    icon = vectorResource(Res.drawable.ic_vk),
                    sizeIcon = 36.dp,
                    onClick = onClickAuthVk
                )
            } else if (platformName == Platform.Name.IOS) {
                AuthButton(
                    text = stringResource(Res.string.continue_apple_id),
                    icon = painterResource(Res.drawable.appleid_button),
                    sizeIcon = 36.dp,
                    onClick = onClickAuthApple
                )
            }

            AuthButton(
                text = stringResource(Res.string.continue_google),
                icon = vectorResource(Res.drawable.ic_google),
                sizeIcon = 28.dp,
                onClick = onClickAuthGoogle
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth(.9F)
                .padding(vertical = 16.dp),
            text = buildAnnotatedString {
                append(stringResource(Res.string.agree_text))

                withLink(
                    LinkAnnotation.Clickable(
                        tag = "clickable_privacy_policy",
                        linkInteractionListener = { onClickPrivacyPolicy() }
                    )
                ) {
                    withStyle(SpanStyle(color = ElectricViolet)) {
                        append(stringResource(Res.string.privacy_policy))
                    }
                }

                append(stringResource(Res.string.and_text))

                withLink(
                    LinkAnnotation.Clickable(
                        tag = "clickable_terms",
                        linkInteractionListener = { onClickTermsOfUse() }
                    )
                ) {
                    withStyle(SpanStyle(color = ElectricViolet)) {
                        append(stringResource(Res.string.terms_of_use))
                    }
                }
            },
            fontSize = 14.tdp,
            textAlign = TextAlign.Center,
            lineHeight = 16.tdp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F)
        )
    }
}

@Composable
private fun AuthButton(
    text: String,
    icon: Any,
    sizeIcon: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(64.dp),
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }

    Button(
        modifier = modifier,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (icon is ImageVector) {
                Image(
                    modifier = Modifier
                        .size(sizeIcon)
                        .align(Alignment.Center)
                        .offset(x = -textWidth / 2 - 30.dp),
                    imageVector = icon,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else if (icon is Painter) {
                Image(
                    modifier = Modifier
                        .size(sizeIcon)
                        .align(Alignment.Center)
                        .offset(x = -textWidth / 2 - 30.dp),
                    painter = icon,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }

            Text(
                modifier = Modifier
                    .onGloballyPositioned { coord ->
                        with(density) { textWidth = coord.size.width.toDp() }
                    },
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.tdp
            )
        }
    }
}

@Preview
@Composable
private fun AuthScreenContentPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AuthScreenContent(
                onClickPrivacyPolicy = {},
                onClickTermsOfUse = {},
                onClickAuthVk = {},
                onClickAuthGoogle = {},
                onClickAuthApple = {}
            )
        }
    }
}