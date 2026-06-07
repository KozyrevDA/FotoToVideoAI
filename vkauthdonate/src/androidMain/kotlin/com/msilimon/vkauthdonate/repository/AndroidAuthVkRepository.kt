package com.msilimon.vkauthdonate.repository

import com.msilimon.vkauthdonate.VkApi
import com.msilimon.vkauthdonate.data.AuthVkRepository
import com.msilimon.vkauthdonate.data.VKAuthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AndroidAuthVkRepository : AuthVkRepository {
    private val _vkAuthResultFlow = MutableStateFlow<VKAuthResult?>(null)
    private val _vkSubFlow = MutableStateFlow(false)
    override val vkAuthResultFlow = _vkAuthResultFlow.asStateFlow()
    override val vkSubFlow: StateFlow<Boolean> = _vkSubFlow.asStateFlow()

    override fun update(vkAuthResult: VKAuthResult) {
        _vkAuthResultFlow.value = vkAuthResult

        if (vkAuthResult is VKAuthResult.Logout) {
            CoroutineScope(Dispatchers.IO).launch {
                VkApi.logout()
            }
        }
    }

    override fun update(isVkSubEnabled: Boolean) {
        _vkSubFlow.value = isVkSubEnabled
    }

    override fun getIdVk(): String? = VkApi.getIdVk()

    override fun getSubVk(): Boolean = VkApi.getSubVk()
}