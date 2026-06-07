package ui.screens.paywall.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.prefs.SharedPrefs
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.managers.SubscriptionManager

class PaywallViewModel(
    private val subscriptionManager: SubscriptionManager,
    private val sharedPrefs: SharedPrefs,
    private val appRepository: AppRepository,
) : ViewModel() {
    val productsRuStore = subscriptionManager.productsRuStore
    val productsGoogleBilling = subscriptionManager.productDetailsSetFlow
    val productsRevCatBilling = subscriptionManager.productDetailsRevCatListFlow

    private val _monthSubCoins = MutableStateFlow<Int?>(null)
    val monthSubCoins = _monthSubCoins.asStateFlow()

    private val _showMoreTokensButtonWhenNonSub = MutableStateFlow<Boolean?>(null)
    val showMoreTokensButtonWhenNonSub = _showMoreTokensButtonWhenNonSub.asStateFlow()

    val isPastAuth get() = sharedPrefs.isPastAuth()
    val isProSub = appRepository.isProSub

    init {
        viewModelScope.launch {
            _monthSubCoins.value = appRepository.getMonthSubCoins()
            _showMoreTokensButtonWhenNonSub.value =
                appRepository.getShowMoreTokensButtonWhenNonSub()
        }
    }

    fun onClickSubDiscount(activity: Any?) {
        subscriptionManager.onClickSubDiscount(activity = activity)
    }

    fun onClickSub(activity: Any?) {
        subscriptionManager.onClickSub(activity = activity)
    }

    fun onClickBuy1000Tokens(activity: Any?) {
        subscriptionManager.onClickBuyAnyTokens(activity = activity, count = 1000)
    }

    fun onClickBuy2000Tokens(activity: Any?) {
        subscriptionManager.onClickBuyAnyTokens(activity = activity, count = 2000)
    }

    fun updateProSub() {
        viewModelScope.launch {
            val result = subscriptionManager.isHaveSubPremium()
            appRepository.putLastIsProSub(result)
        }
    }
}