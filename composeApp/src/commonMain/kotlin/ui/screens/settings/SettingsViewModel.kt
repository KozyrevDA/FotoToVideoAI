package ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.prefs.SharedPrefs
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.managers.SubscriptionManager

class SettingsViewModel(
    private val appRepository: AppRepository,
    private val sharedPrefs: SharedPrefs,
    private val subscriptionManager: SubscriptionManager,
) : ViewModel() {
    private val _countCoins = MutableStateFlow<Int?>(appRepository.countCoins.value)
    val countCoins = _countCoins.asStateFlow()

    private val _email = MutableStateFlow<String?>(null)
    val email = _email.asStateFlow()

    val isProSub = appRepository.isProSub
    val isPastAuth get() = sharedPrefs.isPastAuth()

    fun logout() {
        sharedPrefs.setPastOnboarding(false)
        appRepository.logout()
    }

    fun updateCoins() {
        viewModelScope.launch {
            val coins = appRepository.getUser()?.coins ?: return@launch
            _countCoins.value = coins
        }
    }

    fun updateProSub() {
        viewModelScope.launch {
            val result = subscriptionManager.isHaveSubPremium()
            appRepository.putLastIsProSub(result)
        }
    }

    fun updateEmail() {
        viewModelScope.launch {
            _email.value = appRepository.getUser()?.email
        }
    }

    private val _showTrialGeneration = MutableStateFlow(sharedPrefs.isShowTrialGeneration())
    val showTrialGeneration = _showTrialGeneration.asStateFlow()

    fun setShowTrialGeneration(value: Boolean) {
        sharedPrefs.putShowTrialGeneration(value)
        _showTrialGeneration.value = value
    }

    suspend fun deleteAccount(): Boolean {
        return appRepository.deleteAccount()
    }
}