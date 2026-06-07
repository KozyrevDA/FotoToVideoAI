package ui.screens.settings

import BackHandlerNavigation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.Constants.URL_APP_RUSTORE
import app.Constants.URL_PRIVACY_POLICY
import app.Constants.URL_TERMS_OF_USE
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.about_app
import phototovideoai.composeapp.generated.resources.delete_account
import phototovideoai.composeapp.generated.resources.delete_account_warning
import phototovideoai.composeapp.generated.resources.ic_arrow_next
import phototovideoai.composeapp.generated.resources.ic_exit
import phototovideoai.composeapp.generated.resources.ic_request
import phototovideoai.composeapp.generated.resources.ic_share
import phototovideoai.composeapp.generated.resources.ic_user
import phototovideoai.composeapp.generated.resources.ic_warn
import phototovideoai.composeapp.generated.resources.logout_confirm
import phototovideoai.composeapp.generated.resources.no
import phototovideoai.composeapp.generated.resources.onb_back
import phototovideoai.composeapp.generated.resources.privacy_policy_2
import phototovideoai.composeapp.generated.resources.request_feature
import phototovideoai.composeapp.generated.resources.settings
import phototovideoai.composeapp.generated.resources.share_app
import phototovideoai.composeapp.generated.resources.support
import phototovideoai.composeapp.generated.resources.terms_of_use_full
import phototovideoai.composeapp.generated.resources.yes
import phototovideoai.composeapp.generated.resources.your_account
import getShareProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import openUrl
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ui.navigation.AppNavigationActions
import ui.screens.history.Top
import ui.theme.MidnightShadow
import ui.theme.PhotoToVideoAiTheme
import ui.theme.TomatoRed
import utils.events.Events
import utils.extensions.tdp

@Composable
fun SettingsScreen(
    navigationActions: AppNavigationActions,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val countCoins = viewModel.countCoins.collectAsState(Dispatchers.Main)
    val emailState = viewModel.email.collectAsState(Dispatchers.Main)
    val isProSubState = viewModel.isProSub.collectAsState(Dispatchers.Main)
    val showTrialGeneration by viewModel.showTrialGeneration.collectAsState()
    val messageLogout = stringResource(Res.string.logout_confirm)
    val messageDeleteAccount = stringResource(Res.string.delete_account).plus("?")
    val actionLabel = stringResource(Res.string.yes)
    var showDeleteAccountHint by remember { mutableStateOf(false) }

    BackHandlerNavigation {
        if (snackbarHostState.currentSnackbarData == null) {
            navigationActions.back()
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.updateCoins()
        viewModel.updateProSub()
        viewModel.updateEmail()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SettingsScreenContent(
            modifier = Modifier.fillMaxSize()
                .run {
                    if (showDeleteAccountHint) blur(12.dp) else this
                },
            isPro = isProSubState.value,
            countCoins = countCoins.value,
            email = emailState.value,
            isPastAuth = viewModel.isPastAuth,
            showTrialGeneration = showTrialGeneration,
            onTrialGenerationChanged = { viewModel.setShowTrialGeneration(it) },
            onClickCountCoins = {
                navigationActions.navigateToPaywallScreen(prevScreen = "экран_настроек")
                Events.put(Events.SCREEN_7_SETTINGS_TO_SUB)
            },
            onClickContacts = {},
            onClickRequestFunc = {
                navigationActions.navigateToRequestFuncScreen()
                Events.put(Events.SCREEN_7_SETTINGS_REQUEST_FUNC)
            },
            onClickShare = {
                getShareProvider()?.shareApp()
                Events.put(Events.SCREEN_7_SETTINGS_SHARE_APP)
            },
            onClickReview = {
                openUrl(URL_APP_RUSTORE)
                Events.put(Events.SCREEN_7_SETTINGS_REVIEW)
            },
            onClickPrivacyPolicy = {
                openUrl(URL_PRIVACY_POLICY)
                Events.put(Events.SCREEN_7_SETTINGS_PRIVACY_POLICY)
            },
            onClickTermsOfUse = {
                openUrl(URL_TERMS_OF_USE)
                Events.put(Events.SCREEN_7_SETTINGS_TERMS_OF_USE)
            },
            onClickLogout = {
                coroutineScope.launch {
                    if (snackbarHostState.currentSnackbarData == null) {
                        val result = snackbarHostState.showSnackbar(
                            message = messageLogout,
                            actionLabel = actionLabel,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )

                        if (result == ActionPerformed) {
                            viewModel.logout()
                            navigationActions.back()
                            Events.put(Events.SCREEN_7_SETTINGS_LOGOUT)
                        }
                    }
                }
            },
            onClickDeleteAccount = {
                coroutineScope.launch {
                    if (snackbarHostState.currentSnackbarData == null) {
                        val result = snackbarHostState.showSnackbar(
                            message = messageDeleteAccount,
                            actionLabel = actionLabel,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )

                        if (result == ActionPerformed) {
                            showDeleteAccountHint = true
                        }
                    }
                }
            }
        )

        if (showDeleteAccountHint) {
            DeleteAccountHint(
                onClickDeleteAccountYes = {
                    coroutineScope.launch {
                        Events.put(Events.SCREEN_7_SETTINGS_DELETE_ACCOUNT)
                        viewModel.deleteAccount()
                        navigationActions.back()
                    }
                },
                onClickDeleteAccountNo = { showDeleteAccountHint = false }
            )
        }
    }
}

@Composable
private fun SettingsScreenContent(
    isPro: Boolean,
    countCoins: Int?,
    email: String?,
    isPastAuth: Boolean,
    showTrialGeneration: Boolean,
    onTrialGenerationChanged: (Boolean) -> Unit,
    onClickContacts: () -> Unit,
    onClickRequestFunc: () -> Unit,
    onClickShare: () -> Unit,
    onClickReview: () -> Unit,
    onClickPrivacyPolicy: () -> Unit,
    onClickTermsOfUse: () -> Unit,
    onClickLogout: () -> Unit,
    onClickCountCoins: () -> Unit,
    onClickDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.verticalScroll(scrollState),
        contentAlignment = Alignment.TopCenter
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
                title = stringResource(Res.string.settings),
                isPro = isPro,
                countCoins = countCoins,
                onClickCountCoins = onClickCountCoins
            )

            SettingsPanel(title = stringResource(Res.string.support)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .04F),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = .8.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    /*SettingsPanelRow(
                        text = "Контакты",
                        icon = vectorResource(Res.drawable.ic_email),
                        onClick = { onClickContacts() }
                    )
                    HorizontalDivider(
                        thickness = .8.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F)
                    )*/
                    SettingsPanelRow(
                        text = stringResource(Res.string.request_feature),
                        icon = vectorResource(Res.drawable.ic_request),
                        onClick = { onClickRequestFunc() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsPanel(title = stringResource(Res.string.about_app)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .04F),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .border(
                            width = .8.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    SettingsPanelRow(
                        text = stringResource(Res.string.share_app),
                        icon = vectorResource(Res.drawable.ic_share),
                        onClick = { onClickShare() }
                    )
                    HorizontalDivider(
                        thickness = .8.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F)
                    )
                    /*SettingsPanelRow(
                        text = stringResource(Res.string.rate_us),
                        icon = vectorResource(Res.drawable.ic_star),
                        onClick = { onClickReview() }
                    )*/
                    HorizontalDivider(
                        thickness = .8.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F)
                    )
                    SettingsPanelRow(
                        text = stringResource(Res.string.privacy_policy_2),
                        icon = vectorResource(Res.drawable.ic_warn),
                        onClick = { onClickPrivacyPolicy() }
                    )
                    HorizontalDivider(
                        thickness = .8.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F)
                    )
                    SettingsPanelRow(
                        text = stringResource(Res.string.terms_of_use_full),
                        icon = vectorResource(Res.drawable.ic_warn),
                        onClick = { onClickTermsOfUse() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SettingsPanel(title = "Эксперименты") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .04F),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = .8.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Тестовая генерация при старте",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.tdp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = showTrialGeneration,
                            onCheckedChange = onTrialGenerationChanged,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isPastAuth) {
                SettingsPanel(title = stringResource(Res.string.your_account)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .04F),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = .8.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        10.dp,
                                        Alignment.CenterHorizontally
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    alpha = .06F
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .padding(6.dp),
                                            imageVector = vectorResource(Res.drawable.ic_user),
                                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                                            contentDescription = null
                                        )
                                    }

                                    Text(
                                        text = email ?: "",
                                        fontSize = 16.tdp,
                                        lineHeight = 16.tdp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = .06F
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onClickLogout() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .padding(6.dp),
                                        imageVector = vectorResource(Res.drawable.ic_exit),
                                        tint = TomatoRed,
                                        contentDescription = null
                                    )
                                }
                            }

                            HorizontalDivider(
                                thickness = .8.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .08F)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    modifier = Modifier.clickable(onClick = onClickDeleteAccount),
                                    text = stringResource(Res.string.delete_account),
                                    fontSize = 14.tdp,
                                    lineHeight = 14.tdp,
                                    color = TomatoRed,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsPanel(
    title: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = title,
            fontSize = 14.tdp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
            fontWeight = FontWeight.SemiBold
        )

        content()
    }
}

@Composable
private fun SettingsPanelRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Box(
        modifier = modifier.clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .06F),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(34.dp)
                            .padding(6.dp),
                        imageVector = icon,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                        contentDescription = null
                    )
                }

                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.tdp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Image(
                imageVector = vectorResource(Res.drawable.ic_arrow_next),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun DeleteAccountHint(
    onClickDeleteAccountYes: () -> Unit,
    onClickDeleteAccountNo: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1F),
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = shape
            )
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .1F),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.delete_account).plus("?"),
                color = TomatoRed,
                fontSize = 25.tdp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.delete_account_warning),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 19.tdp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(.9F),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HintButton(
                    modifier = Modifier.weight(1F),
                    onClick = onClickDeleteAccountYes
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.yes),
                        color = TomatoRed,
                        fontSize = 19.tdp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                HintButton(
                    modifier = Modifier.weight(1F),
                    onClick = onClickDeleteAccountNo
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.no),
                        fontSize = 19.tdp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun HintButton(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Button(
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MidnightShadow
        ),
        onClick = onClick
    ) {
        content()
    }
}

@Preview
@Composable
private fun DeleteAccountHintPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DeleteAccountHint(
                onClickDeleteAccountYes = {},
                onClickDeleteAccountNo = {}
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenContentPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SettingsScreenContent(
                isPro = true,
                countCoins = 300,
                email = "email@email.com",
                isPastAuth = true,
                showTrialGeneration = true,
                onTrialGenerationChanged = {},
                onClickContacts = {},
                onClickRequestFunc = {},
                onClickShare = {},
                onClickReview = {},
                onClickPrivacyPolicy = {},
                onClickTermsOfUse = {},
                onClickLogout = {},
                onClickCountCoins = {},
                onClickDeleteAccount = {}
            )
        }
    }
}