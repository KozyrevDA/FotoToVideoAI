package ui.screens.paywall.tokens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.model.TokenData
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.back_button
import phototovideoai.composeapp.generated.resources.coin_big
import phototovideoai.composeapp.generated.resources.currency_rub
import phototovideoai.composeapp.generated.resources.generation_cost
import phototovideoai.composeapp.generated.resources.improve_cost
import phototovideoai.composeapp.generated.resources.paywall_back
import phototovideoai.composeapp.generated.resources.photo_cost
import phototovideoai.composeapp.generated.resources.purchasing_tokens
import phototovideoai.composeapp.generated.resources.question_button
import phototovideoai.composeapp.generated.resources.use_tokens_for_generation
import phototovideoai.composeapp.generated.resources.what_are_tokens
import phototovideoai.composeapp.generated.resources.your_balance
import ui.navigation.AppNavigationActions
import ui.theme.CosmicAbyss
import ui.theme.GoldenAmber
import ui.theme.MidnightShadow
import ui.theme.PhotoToVideoAiTheme
import utils.extensions.ResponsiveText
import utils.extensions.tdp

@Composable
fun TokensScreen(
    navigationActions: AppNavigationActions,
    activity: Any?,
    viewModel: TokensViewModel = koinViewModel(),
) {
    val tokensRuStoreState = viewModel.tokensRuStoreState.collectAsState(Dispatchers.Main)
    val countCoins = viewModel.countCoins.collectAsState(Dispatchers.Main)
    var showTokensHint by remember { mutableStateOf(false) }

    TokensScreenContent(
        isBlur = showTokensHint,
        balanceValue = countCoins.value?.toString() ?: "-",
        tokensRuStoreState = tokensRuStoreState.value,
        onClickBack = { navigationActions.back() },
        onClickQuestion = { showTokensHint = true },
        onClickToken = {
            if (viewModel.isPastAuth) {
                viewModel.onClickBuyAnyTokens(activity = activity, count = it)
            } else {
                navigationActions.navigateToAuthScreen()
            }
        }
    )

    if (showTokensHint) {
        TokensHint(onClickBack = { showTokensHint = false })
    }
}

@Composable
private fun TokensScreenContent(
    isBlur: Boolean,
    balanceValue: String,
    tokensRuStoreState: List<Pair<TokenData, TokenData>>,
    onClickBack: () -> Unit,
    onClickQuestion: () -> Unit,
    onClickToken: (Int) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(color = MidnightShadow),
) {
    val tokens = if (tokensRuStoreState.isNotEmpty()) {
        tokensRuStoreState.map { pair ->
            pair.first.copy(price = pair.first.price + stringResource(Res.string.currency_rub)) to
                    pair.second.copy(price = pair.second.price + stringResource(Res.string.currency_rub))
        }
    } else {
        emptyList()
    }

    Box(
        modifier = modifier.run {
            if (isBlur) blur(8.dp) else this
        },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.5F)
                .align(Alignment.TopCenter),
            painter = painterResource(Res.drawable.paywall_back),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Top(
                onClickBack = onClickBack,
                onClickQuestion = onClickQuestion
            )

            Spacer(modifier = Modifier.height(10.dp))

            Balance(value = balanceValue)

            Spacer(modifier = Modifier.height(10.dp))

            PricesPanel(
                tokens = tokens,
                onClick = onClickToken
            )
        }
    }
}

@Composable
private fun Top(
    onClickBack: () -> Unit,
    onClickQuestion: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(top = 60.dp),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(42.dp),
            onClick = onClickBack
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.back_button),
                contentDescription = null
            )
        }

        ResponsiveText(
            text = stringResource(Res.string.purchasing_tokens),
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(
                fontSize = 20.tdp,
                fontWeight = FontWeight.Bold
            ),
        )

        IconButton(
            modifier = Modifier.size(42.dp),
            onClick = onClickQuestion
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.question_button),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun Balance(
    value: String,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(Res.drawable.coin_big),
            contentScale = ContentScale.Fit,
            contentDescription = null
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.tdp,
                lineHeight = 21.tdp,
                color = GoldenAmber,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(Res.string.your_balance),
                fontSize = 14.tdp,
                lineHeight = 15.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun PricesPanel(
    tokens: List<Pair<TokenData, TokenData>>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tokens) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriceItem(
                    modifier = Modifier.weight(1F, false),
                    count = pair.first.count,
                    price = pair.first.price,
                    onClick = onClick
                )
                PriceItem(
                    modifier = Modifier.weight(1F, false),
                    count = pair.second.count,
                    price = pair.second.price,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
private fun PriceItem(
    count: Int,
    price: String,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
        .width(174.dp),
) {
    val shape = RoundedCornerShape(30.dp)
    val bottomHeight = 38.dp

    Box(
        modifier = modifier
            .aspectRatio(174F / 154F)
            .border(
                width = 1.dp,
                color = CosmicAbyss,
                shape = shape
            )
            .clip(shape)
            .clickable(onClick = { onClick(count) }),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = bottomHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(40.dp),
                painter = painterResource(Res.drawable.coin_big),
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = count.toString(),
                fontSize = 25.tdp,
                lineHeight = 26.tdp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomHeight)
                .background(
                    color = CosmicAbyss,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = price,
                fontSize = 16.tdp,
                lineHeight = 17.tdp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun TokensHint(
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
                text = stringResource(Res.string.what_are_tokens),
                fontSize = 20.tdp,
                lineHeight = 20.tdp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.use_tokens_for_generation),
                fontSize = 14.tdp,
                lineHeight = 14.tdp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.generation_cost),
                    fontSize = 18.tdp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(Res.string.photo_cost),
                    fontSize = 14.tdp,
                    lineHeight = 17.tdp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                )

                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = stringResource(Res.string.improve_cost),
                    fontSize = 14.tdp,
                    lineHeight = 7.tdp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6F),
                )
            }

            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

@Preview
@Composable
private fun TokensHintPreview() {
    PhotoToVideoAiTheme {
        TokensHint(onClickBack = {})
    }
}

@Preview
@Composable
private fun TokensScreenContentPreview() {
    PhotoToVideoAiTheme {
        TokensScreenContent(
            isBlur = false,
            balanceValue = "100000",
            tokensRuStoreState = listOf(
                Pair(
                    TokenData(100, "10₽"),
                    TokenData(500, "50₽")
                ),
                Pair(
                    TokenData(1000, "100₽"),
                    TokenData(1500, "150₽")
                ),
                Pair(
                    TokenData(2000, "200₽"),
                    TokenData(2500, "250₽")
                ),
                Pair(
                    TokenData(3000, "300₽"),
                    TokenData(3500, "350₽")
                )
            ),
            onClickBack = {},
            onClickQuestion = {},
            onClickToken = {}
        )
    }
}

@Preview
@Composable
private fun PriceItemPreview() {
    PhotoToVideoAiTheme {
        Surface(color = MidnightShadow) {
            PriceItem(
                count = 100,
                price = "₽10",
                onClick = {}
            )
        }
    }
}