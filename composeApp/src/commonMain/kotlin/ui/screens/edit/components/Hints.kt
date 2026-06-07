package ui.screens.edit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.buy_tokens
import phototovideoai.composeapp.generated.resources.coin_big
import phototovideoai.composeapp.generated.resources.delete
import phototovideoai.composeapp.generated.resources.delete_video_message
import phototovideoai.composeapp.generated.resources.delete_video_title
import phototovideoai.composeapp.generated.resources.dont_delete
import phototovideoai.composeapp.generated.resources.error_rejected_message
import phototovideoai.composeapp.generated.resources.error_rejected_title
import phototovideoai.composeapp.generated.resources.ic_warn
import phototovideoai.composeapp.generated.resources.need_tokens_effect
import phototovideoai.composeapp.generated.resources.not_enough_tokens
import phototovideoai.composeapp.generated.resources.ok
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.theme.CosmicAbyss
import ui.theme.PhotoToVideoAiTheme
import ui.theme.TomatoRed
import utils.extensions.tdp

@Composable
fun NotEnoughTokensHint(
    onClickBuy: () -> Unit,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = .3F))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
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
                .verticalScroll(scrollState)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 12.dp)
                    .size(width = 40.dp, height = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .2F),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            Image(
                modifier = Modifier.size(70.dp),
                painter = painterResource(Res.drawable.coin_big),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.not_enough_tokens),
                fontSize = 20.tdp,
                lineHeight = 20.tdp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.need_tokens_effect),
                fontSize = 14.tdp,
                lineHeight = 14.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = CosmicAbyss
                ),
                onClick = onClickBuy
            ) {
                Text(
                    text = stringResource(Res.string.buy_tokens),
                    fontSize = 16.tdp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

@Composable
fun ErrorSafetySystemHint(
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = .3F))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClickClose
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
                .verticalScroll(scrollState)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 12.dp)
                    .size(width = 40.dp, height = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .2F),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            Image(
                modifier = Modifier.size(70.dp),
                painter = painterResource(Res.drawable.ic_warn),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color = TomatoRed),
                contentDescription = null
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.error_rejected_title),
                fontSize = 20.tdp,
                lineHeight = 20.tdp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.error_rejected_message),
                fontSize = 14.tdp,
                lineHeight = 14.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = CosmicAbyss
                ),
                onClick = onClickClose
            ) {
                Text(
                    text = stringResource(Res.string.ok),
                    fontSize = 16.tdp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

@Composable
fun DeleteVideoHint(
    onClickDelete: () -> Unit,
    onClickBack: () -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = .3F))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
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
                .verticalScroll(scrollState)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 12.dp)
                    .size(width = 40.dp, height = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .2F),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            Image(
                modifier = Modifier.size(70.dp),
                painter = painterResource(Res.drawable.ic_warn),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color = TomatoRed),
                contentDescription = null
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.delete_video_title),
                fontSize = 20.tdp,
                lineHeight = 20.tdp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.delete_video_message),
                fontSize = 14.tdp,
                lineHeight = 14.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = TomatoRed
                ),
                onClick = onClickDelete
            ) {
                Text(
                    text = stringResource(Res.string.delete),
                    fontSize = 16.tdp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = CosmicAbyss
                ),
                onClick = onClickBack
            ) {
                Text(
                    text = stringResource(Res.string.dont_delete),
                    fontSize = 16.tdp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

@Preview
@Composable
private fun DeleteVideoHint() {
    PhotoToVideoAiTheme {
        DeleteVideoHint(
            onClickDelete = {},
            onClickBack = {}
        )
    }
}

@Preview
@Composable
private fun NotEnoughTokensHintPreview() {
    PhotoToVideoAiTheme {
        NotEnoughTokensHint(
            onClickBuy = {},
            onClickBack = {}
        )
    }
}

@Preview
@Composable
private fun ErrorSafetySystemHintPreview() {
    PhotoToVideoAiTheme {
        ErrorSafetySystemHint(onClickClose = {})
    }
}