package com.msilimon.vkauthdonate.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val SHARED_PREFS_NAME = "vk_auth_donate_prefs"
private const val PRESSED_AUTH_VK_BUTTON = "pressed_auth_vk_button"
const val SUB_ON_VK = "sub_on_vk"
const val ID_VK = "id_vk"

internal class SharedPreferencesImpl(
    context: Context,
    sharedPrefsName: String? = null
) : SharedPreferences {
    private val prefs = context.getSharedPreferences(
        sharedPrefsName ?: SHARED_PREFS_NAME, MODE_PRIVATE
    )

    override fun setPressedAuthVkButton(value: Boolean) {
        prefs.edit().putBoolean(PRESSED_AUTH_VK_BUTTON, value).apply()
    }

    override fun isPressedAuthVkButton(): Boolean {
        return prefs.getBoolean(PRESSED_AUTH_VK_BUTTON, false)
    }

    override fun setSubVk(value: Boolean) {
        prefs.edit().putBoolean(SUB_ON_VK, value).apply()
    }

    override fun getSubVk(): Boolean {
        return prefs.getBoolean(SUB_ON_VK, false)
    }

    override fun putIdVk(value: String) {
        prefs.edit().putString(ID_VK, value).apply()
    }

    override fun getIdVk(): String? {
        return prefs.getString(ID_VK, null)
    }
}