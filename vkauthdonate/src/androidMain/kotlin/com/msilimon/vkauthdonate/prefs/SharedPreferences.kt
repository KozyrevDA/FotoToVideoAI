package com.msilimon.vkauthdonate.prefs

internal interface SharedPreferences {
    fun setPressedAuthVkButton(value: Boolean)
    fun isPressedAuthVkButton(): Boolean
    fun setSubVk(value: Boolean)
    fun getSubVk(): Boolean
    fun putIdVk(value: String)
    fun getIdVk(): String?
}