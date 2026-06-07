package ui.screens.paywall.tokens

import BUY_14000_TOKENS
import BUY_1600_TOKENS
import BUY_2600_TOKENS
import BUY_400_TOKENS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.TokenData
import data.prefs.SharedPrefs
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import utils.managers.SubscriptionManager

class TokensViewModel(
    private val appRepository: AppRepository,
    private val subscriptionManager: SubscriptionManager,
    private val sharedPrefs: SharedPrefs,
) : ViewModel() {
    private val _countCoins = MutableStateFlow(appRepository.countCoins.value)
    val countCoins = _countCoins.asStateFlow()

    private val _tokensRuStoreState = MutableStateFlow<List<Pair<TokenData, TokenData>>>(
        value = emptyList()
    )
    val tokensRuStoreState = _tokensRuStoreState.asStateFlow()
    val isPastAuth get() = sharedPrefs.isPastAuth()

    init {
        viewModelScope.launch {
            updateTokensRuStore()
        }
    }

    fun onClickBuyAnyTokens(activity: Any?, count: Int) {
        subscriptionManager.onClickBuyAnyTokens(activity = activity, count = count)
    }

    private suspend fun updateTokensRuStore() {
        val products = subscriptionManager.productsRuStore.first()
        val tokens = listOfNotNull(
            products.find { it.productId == BUY_400_TOKENS }?.price?.let {
                TokenData(count = 400, price = it.div(100).toString())
            },
            products.find { it.productId == BUY_1600_TOKENS }?.price?.let {
                TokenData(count = 1600, price = it.div(100).toString())
            },
            products.find { it.productId == BUY_2600_TOKENS }?.price?.let {
                TokenData(count = 2600, price = it.div(100).toString())
            },
            products.find { it.productId == BUY_14000_TOKENS }?.price?.let {
                TokenData(count = 14000, price = it.div(100).toString())
            },
        )

        val pairedTokens = tokens.chunked(2).mapNotNull {
            if (it.size == 2) it[0] to it[1]
            else null
        }

        _tokensRuStoreState.value = pairedTokens
    }
}