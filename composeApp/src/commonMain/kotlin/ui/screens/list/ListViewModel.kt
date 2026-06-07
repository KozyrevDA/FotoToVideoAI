package ui.screens.list

import Language
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.Template
import data.repository.AppRepository
import getLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.managers.SubscriptionManager

class ListViewModel(
    private val appRepository: AppRepository,
    private val subscriptionManager: SubscriptionManager,
) : ViewModel() {
    private val _countCoins = MutableStateFlow<Int?>(appRepository.countCoins.value)
    val countCoins = _countCoins.asStateFlow()

    private val _templatesMap = MutableStateFlow<Map<String, List<Template>>>(emptyMap())
    val templatesMap = _templatesMap.asStateFlow()

    val isProSub = appRepository.isProSub
    val isInstalledFromRuStore = subscriptionManager.isInstalledFromRuStore()

    init {
        updateCoins()
        updateProSub()
        updateFiltersList()
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

    private fun updateFiltersList() {
        viewModelScope.launch(Dispatchers.Default) {
            _templatesMap.value = appRepository.getAllTemplatesMP4().groupBy {
                when (getLanguage()) {
                    Language.RU, Language.BE, Language.KK, Language.UK -> it.groupRu
                    Language.PT -> it.groupPt
                    Language.EN -> it.groupEn
                }
            }
        }
    }
}