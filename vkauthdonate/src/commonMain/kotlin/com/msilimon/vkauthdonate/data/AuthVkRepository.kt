package com.msilimon.vkauthdonate.data

import kotlinx.coroutines.flow.StateFlow

interface AuthVkRepository {
    val vkAuthResultFlow: StateFlow<VKAuthResult?>
    val vkSubFlow: StateFlow<Boolean>

    fun update(vkAuthResult: VKAuthResult)
    fun update(isVkSubEnabled: Boolean)

    fun getIdVk(): String?
    fun getSubVk(): Boolean
}