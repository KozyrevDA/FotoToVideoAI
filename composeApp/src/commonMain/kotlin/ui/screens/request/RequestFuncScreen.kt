package ui.screens.request

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.describe_feature
import phototovideoai.composeapp.generated.resources.request_feature
import phototovideoai.composeapp.generated.resources.send
import phototovideoai.composeapp.generated.resources.suggest_feature_description
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sendEmailToDefaultApp
import ui.navigation.AppNavigationActions
import ui.theme.CosmicAbyss
import utils.events.Events
import utils.extensions.tdp

@Composable
fun RequestFuncScreen(
    navigationActions: AppNavigationActions,
    viewModel: RequestFuncViewModel = koinViewModel(),
) {
    val textRequestState by viewModel.textRequest.collectAsState(Dispatchers.Main)

    RequestFuncScreenContent(
        textRequest = textRequestState,
        onClickBack = {
            navigationActions.back()
            Events.put(Events.SCREEN_8_REQUEST_BACK)
        },
        onTextRequest = viewModel::onTextRequest,
    )
}

@Composable
private fun RequestFuncScreenContent(
    textRequest: String,
    onClickBack: () -> Unit,
    onTextRequest: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClickBack
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    color = CosmicAbyss,
                    shape = shape
                )
                .clickable(
                    enabled = false,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = stringResource(Res.string.request_feature),
                fontSize = 20.tdp,
                lineHeight = 20.tdp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = stringResource(Res.string.suggest_feature_description),
                fontSize = 14.tdp,
                lineHeight = 15.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 180.dp)
                    .padding(16.dp)
                    .border(
                        width = .8.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .1F),
                        shape = RoundedCornerShape(10.dp)
                    ),
                value = textRequest,
                onValueChange = onTextRequest,
                placeholder = {
                    Text(
                        text = stringResource(Res.string.describe_feature),
                        color = Color.Gray
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CosmicAbyss,
                    unfocusedContainerColor = CosmicAbyss,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                    cursorColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = false,
                maxLines = 7
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = CosmicAbyss
                ),
                onClick = {
                    sendEmailToDefaultApp(textRequest)
                    Events.put(Events.SCREEN_8_REQUEST_FUNC_SEND)
                }
            ) {
                Text(
                    text = stringResource(Res.string.send),
                    fontSize = 16.tdp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(84.dp))
        }
    }
}