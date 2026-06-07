package ui.screens.paywall.main

import BUY_1000_TOKENS_GOOGLE
import BUY_2000_TOKENS_GOOGLE
import MONTH_SUB
import Platform
import SUB_MONTH_GOOGLE
import SUB_YEAR_GOOGLE
import YEAR_SUB
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.model.PaywallPrices
import data.model.billing.google.ProductDetailsGoogle
import data.model.billing.revcat.ProductDetails
import data.model.billing.rustore.ProductRuStore
import getPlatform
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import phototovideoai.composeapp.generated.resources.Res
import phototovideoai.composeapp.generated.resources.ads_free_experience
import phototovideoai.composeapp.generated.resources.back_button
import phototovideoai.composeapp.generated.resources.coin_big
import phototovideoai.composeapp.generated.resources.currency_rub
import phototovideoai.composeapp.generated.resources.currency_rub_month
import phototovideoai.composeapp.generated.resources.currency_rub_year
import phototovideoai.composeapp.generated.resources.discount_83_off
import phototovideoai.composeapp.generated.resources.full_access
import phototovideoai.composeapp.generated.resources.generate_with_2900_tokens
import phototovideoai.composeapp.generated.resources.higher_generation_speed
import phototovideoai.composeapp.generated.resources.ic_crown_sub
import phototovideoai.composeapp.generated.resources.ic_discount
import phototovideoai.composeapp.generated.resources.ic_endless
import phototovideoai.composeapp.generated.resources.ic_flame
import phototovideoai.composeapp.generated.resources.ic_lightning
import phototovideoai.composeapp.generated.resources.ic_star_green
import phototovideoai.composeapp.generated.resources.monthly
import phototovideoai.composeapp.generated.resources.more_tokens
import phototovideoai.composeapp.generated.resources.no_limits_no_ads
import phototovideoai.composeapp.generated.resources.paywall_back
import phototovideoai.composeapp.generated.resources.per_month
import phototovideoai.composeapp.generated.resources.plus_2900_tokens
import phototovideoai.composeapp.generated.resources.pro_version
import phototovideoai.composeapp.generated.resources.save_time
import phototovideoai.composeapp.generated.resources.terms_of_use_full
import phototovideoai.composeapp.generated.resources.tokens_1000
import phototovideoai.composeapp.generated.resources.tokens_2000
import phototovideoai.composeapp.generated.resources.yearly
import ui.components.DarkSubButton
import ui.components.DarkSubDiscountButton
import ui.navigation.AppNavigationActions
import ui.theme.AmberFlame
import ui.theme.LimeBurst
import ui.theme.MidnightShadow
import ui.theme.TropicalMint
import utils.billing.revcat.RevCat
import utils.events.Events
import utils.extensions.tdp
import kotlin.math.round

@Composable
fun PaywallScreen(
    prevScreen: String,
    navigationActions: AppNavigationActions,
    activity: Any?,
    viewModel: PaywallViewModel = koinViewModel(),
) {
    val productsRuStoreState = viewModel.productsRuStore.collectAsState(Dispatchers.Main)
    val productsGoogleBilling = viewModel.productsGoogleBilling.collectAsState(Dispatchers.Main)
    val productsRevCatBilling = viewModel.productsRevCatBilling.collectAsState(Dispatchers.Main)
    val monthSubCoinsState by viewModel.monthSubCoins.collectAsState(Dispatchers.Main)
    val isProSubState by viewModel.isProSub.collectAsState(Dispatchers.Main)
    val showMoreTokensButtonWhenNonSubState by viewModel.showMoreTokensButtonWhenNonSub.collectAsState(
        Dispatchers.Main
    )

    LaunchedEffect(Unit) {
        viewModel.updateProSub()
    }

    PaywallScreenContent(
        productsRuStore = productsRuStoreState.value,
        productsGoogleBilling = productsGoogleBilling.value.toList(),
        productsRevCatBilling = productsRevCatBilling.value,
        monthSubCoins = monthSubCoinsState,
        isProSub = isProSubState,
        showMoreTokensButtonWhenNonSub = showMoreTokensButtonWhenNonSubState,
        onClickBack = {
            navigationActions.back()
            Events.put(Events.SCREEN_9_PAYWALL_BACK)
        },
        onClickSubDiscount = {
            if (viewModel.isPastAuth) {
                viewModel.onClickSubDiscount(activity)
                Events.put(
                    Events.SCREEN_9_PAYWALL_SUB_MONTH,
                    mapOf("предыдущий_экран" to prevScreen)
                )
            } else {
                navigationActions.navigateToAuthScreen()
            }
        },
        onClickSub = {
            if (viewModel.isPastAuth) {
                viewModel.onClickSub(activity)
                Events.put(
                    Events.SCREEN_9_PAYWALL_SUB_YEAR,
                    mapOf("предыдущий_экран" to prevScreen)
                )
            } else {
                navigationActions.navigateToAuthScreen()
            }
        },
        onClickBuy1000Tokens = {
            if (viewModel.isPastAuth) {
                viewModel.onClickBuy1000Tokens(activity)
                Events.put(
                    Events.SCREEN_9_PAYWALL_BUY_1000_TOKENS,
                    mapOf("предыдущий_экран" to prevScreen)
                )
            } else {
                navigationActions.navigateToAuthScreen()
            }
        },
        onClickBuy2000Tokens = {
            if (viewModel.isPastAuth) {
                viewModel.onClickBuy2000Tokens(activity)
                Events.put(
                    Events.SCREEN_9_PAYWALL_BUY_2000_TOKENS,
                    mapOf("предыдущий_экран" to prevScreen)
                )
            } else {
                navigationActions.navigateToAuthScreen()
            }
        },
        onClickMoreTokens = {
            navigationActions.navigateToTokensScreen()
        }
    )
}

@Composable
private fun PaywallScreenContent(
    productsRuStore: List<ProductRuStore>,
    productsGoogleBilling: List<ProductDetailsGoogle>,
    productsRevCatBilling: List<ProductDetails>,
    monthSubCoins: Int?,
    isProSub: Boolean,
    showMoreTokensButtonWhenNonSub: Boolean?,
    onClickBack: () -> Unit,
    onClickSubDiscount: () -> Unit,
    onClickSub: () -> Unit,
    onClickBuy1000Tokens: () -> Unit,
    onClickBuy2000Tokens: () -> Unit,
    onClickMoreTokens: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(color = MidnightShadow),
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier,
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Top(onClickBack = onClickBack)
            TitlePage()
            Spacer(modifier = Modifier.height(32.dp))
            DescPanel(monthSubCoins)
            Spacer(modifier = Modifier.height(32.dp))
            SubPanel(
                productsRuStore = productsRuStore,
                productsGoogleBilling = productsGoogleBilling,
                productsRevCatBilling = productsRevCatBilling,
                isProSub = isProSub,
                showMoreTokensButtonWhenNonSub = showMoreTokensButtonWhenNonSub,
                onClickSub = onClickSub,
                onClickSubDiscount = onClickSubDiscount,
                onClickBuy1000Tokens = onClickBuy1000Tokens,
                onClickBuy2000Tokens = onClickBuy2000Tokens,
                onClickMoreTokens = onClickMoreTokens
            )
            Spacer(modifier = Modifier.height(32.dp))
            Footer()
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
        contentAlignment = Alignment.BottomStart
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
    }
}

@Composable
private fun TitlePage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier.size(70.dp),
            imageVector = vectorResource(Res.drawable.ic_crown_sub),
            contentDescription = null
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.pro_version),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.tdp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.full_access),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
            fontSize = 14.tdp,
            lineHeight = 15.tdp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DescPanel(
    monthSubCoins: Int?,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        DescPanelItem(
            title = stringResource(Res.string.plus_2900_tokens)
                .replace("2 900", monthSubCoins?.toString() ?: "~"),
            desc = stringResource(Res.string.generate_with_2900_tokens)
                .replace("2 900", monthSubCoins?.toString() ?: "~"),
            icon = vectorResource(Res.drawable.ic_star_green),
            colorTitle = TropicalMint
        )
        DescPanelItem(
            title = stringResource(Res.string.higher_generation_speed),
            desc = stringResource(Res.string.save_time),
            icon = vectorResource(Res.drawable.ic_flame),
            colorTitle = AmberFlame
        )
        DescPanelItem(
            title = stringResource(Res.string.no_limits_no_ads),
            desc = stringResource(Res.string.ads_free_experience),
            icon = vectorResource(Res.drawable.ic_endless),
            colorTitle = LimeBurst
        )
    }
}

@Composable
private fun DescPanelItem(
    title: String,
    desc: String,
    icon: ImageVector,
    colorTitle: Color,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            imageVector = icon,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 6.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                color = colorTitle,
                fontSize = 16.tdp,
                lineHeight = 16.tdp,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = desc,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                fontSize = 14.tdp,
                lineHeight = 15.tdp,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun SubPanel(
    productsRuStore: List<ProductRuStore>,
    productsGoogleBilling: List<ProductDetailsGoogle>,
    productsRevCatBilling: List<ProductDetails>,
    isProSub: Boolean,
    showMoreTokensButtonWhenNonSub: Boolean?,
    onClickSubDiscount: () -> Unit,
    onClickSub: () -> Unit,
    onClickBuy1000Tokens: () -> Unit,
    onClickBuy2000Tokens: () -> Unit,
    onClickMoreTokens: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    val paywallPrices = if (productsRuStore.isNotEmpty()) {
        val monthPair = Pair(
            first = (productsRuStore.find { it.productId == MONTH_SUB }?.price
                ?.div(100)
                ?.toString() ?: "") + stringResource(Res.string.currency_rub),
            second = (productsRuStore.find { it.productId == MONTH_SUB }?.price
                ?.div(100)
                ?.toString() ?: "") + stringResource(Res.string.currency_rub_month)
        )
        val yearPair = Pair(
            first = (productsRuStore.find { it.productId == YEAR_SUB }?.price
                ?.div(100)
                ?.toString() ?: "") + stringResource(Res.string.currency_rub_year),
            second = (productsRuStore.find { it.productId == YEAR_SUB }?.price
                ?.div(1200)
                ?.toString() ?: "") + stringResource(Res.string.currency_rub_month)
        )

        PaywallPrices(
            monthPair = monthPair,
            yearPair = yearPair,
        )
    } else if (productsGoogleBilling.isNotEmpty()) {
        val subMonth = productsGoogleBilling.find { it.productId == SUB_MONTH_GOOGLE }
        val subYear = productsGoogleBilling.find { it.productId == SUB_YEAR_GOOGLE }
        val buy1000Tokens = productsGoogleBilling.find { it.productId == BUY_1000_TOKENS_GOOGLE }
        val buy2000Tokens = productsGoogleBilling.find { it.productId == BUY_2000_TOKENS_GOOGLE }

        PaywallPrices(
            monthPair = Pair(
                first = (subMonth?.fullPriceMicros
                    ?.div(1_000_000)
                    ?.toString() ?: "") + " " + (subMonth?.priceCurrencyCode ?: ""),
                second = (subMonth?.fullPriceMicros
                    ?.div(1_000_000)
                    ?.toString()
                    ?: "") + " " + (subMonth?.priceCurrencyCode ?: "")
                        + stringResource(Res.string.per_month)
            ),
            yearPair = Pair(
                first = (subYear?.fullPriceMicros
                    ?.div(1_000_000)
                    ?.toString() ?: "") + " " + (subYear?.priceCurrencyCode ?: ""),
                second = (subYear?.fullPriceMicros
                    ?.div(12_000_000)
                    ?.toString()
                    ?: "") + " " + (subYear?.priceCurrencyCode ?: "") +
                        stringResource(Res.string.per_month)
            ),
            buy1000Tokens = (buy1000Tokens?.fullPriceMicros
                ?.div(1_000_000)
                ?.toString() ?: "") + " " + (buy1000Tokens?.priceCurrencyCode ?: ""),
            buy2000Tokens = (buy2000Tokens?.fullPriceMicros
                ?.div(1_000_000)
                ?.toString() ?: "") + " " + (buy2000Tokens?.priceCurrencyCode ?: "")
        )
    } else if (productsRevCatBilling.isNotEmpty()) {
        val subMonth = productsRevCatBilling.find { it.productId == RevCat.SUBSCRIPTION_MONTH }
        val subYear = productsRevCatBilling.find { it.productId == RevCat.SUBSCRIPTION_YEAR }
        val buy1000Tokens = productsRevCatBilling.find { it.productId == RevCat.BUY_1000_TOKENS }
        val buy2000Tokens = productsRevCatBilling.find { it.productId == RevCat.BUY_2000_TOKENS }

        PaywallPrices(
            monthPair = Pair(
                first = subMonth?.formattedPrice ?: "",
                second = (subMonth?.formattedPrice ?: "") + stringResource(Res.string.per_month)
            ),
            yearPair = Pair(
                first = subYear?.formattedPrice ?: "",
                second = "${
                    subYear?.amount?.div(12)
                        ?.let { (round(it * 100) / 100).toString() } ?: ""
                } ${subYear?.currencyCode ?: ""}${stringResource(Res.string.per_month)}"
            ),
            buy1000Tokens = buy1000Tokens?.formattedPrice ?: "",
            buy2000Tokens = buy2000Tokens?.formattedPrice ?: ""
        )
    } else {
        PaywallPrices(
            monthPair = Pair("", ""),
            yearPair = Pair("", ""),
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DarkSubDiscountButton(
                discount = stringResource(Res.string.discount_83_off),
                onClick = onClickSubDiscount
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        imageVector = vectorResource(Res.drawable.ic_discount),
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(Res.string.monthly),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                        fontSize = 14.tdp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = paywallPrices.monthPair.first,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 25.tdp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = paywallPrices.monthPair.second,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                        fontSize = 14.tdp
                    )
                }
            }

            DarkSubButton(onClick = onClickSub) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        imageVector = vectorResource(Res.drawable.ic_lightning),
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(Res.string.yearly),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                        fontSize = 14.tdp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (productsRuStore.isNotEmpty()) {
                        Text(
                            text = "1${stringResource(Res.string.currency_rub)}",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 25.tdp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = paywallPrices.yearPair.first,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                            fontSize = 14.tdp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = paywallPrices.yearPair.first,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 25.tdp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = paywallPrices.yearPair.second,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                            fontSize = 14.tdp
                        )
                    }
                }
            }
        }

        if (showMoreTokensButtonWhenNonSub == true || isProSub) {
            if (paywallPrices.buy1000Tokens != null && paywallPrices.buy2000Tokens != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DarkSubButton(onClick = onClickBuy1000Tokens) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                modifier = Modifier.size(24.dp),
                                imageVector = vectorResource(Res.drawable.ic_lightning),
                                contentDescription = null
                            )

                            Text(
                                text = stringResource(Res.string.tokens_1000),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                                fontSize = 14.tdp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = paywallPrices.buy1000Tokens,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 25.tdp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    DarkSubButton(onClick = onClickBuy2000Tokens) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                modifier = Modifier.size(24.dp),
                                imageVector = vectorResource(Res.drawable.ic_lightning),
                                contentDescription = null
                            )

                            Text(
                                text = stringResource(Res.string.tokens_2000),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                                fontSize = 14.tdp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = paywallPrices.buy2000Tokens,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 25.tdp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else if (getPlatform().name != Platform.Name.IOS) {
                DarkSubButton(onClick = onClickMoreTokens) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(42.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(Res.drawable.coin_big),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )

                        Text(
                            modifier = Modifier.padding(start = 20.dp),
                            text = stringResource(Res.string.more_tokens),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F),
                            fontSize = 16.tdp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Footer(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        /*Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MidnightShadow
            ),
            onClick = { }
        ) {
            Text(
                text = "Попробовать пробный период 3 дня",
                fontSize = 16.tdp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Пробный период 3 дня бесплатно, затем \$200 в год.\n" +
                    "Можно отменить в любой момент",
            fontSize = 12.tdp,
            lineHeight = 13.tdp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8F)
        )*/

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.terms_of_use_full),
            fontSize = 14.tdp,
            textAlign = TextAlign.Center,
            color = TropicalMint
        )
    }
}