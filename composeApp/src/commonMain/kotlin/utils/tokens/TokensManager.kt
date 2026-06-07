package utils.tokens

import com.msilimon.vkauthdonate.data.VKAuthResult
import data.model.AuthTokens
import data.prefs.SharedPrefs
import getAuthVkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokensManager(private val sharedPrefs: SharedPrefs) {
    private val _logoutState = MutableStateFlow(false)
    val logoutState = _logoutState.asStateFlow()

    fun putAuthTokens(authTokens: AuthTokens) {
        sharedPrefs.putAuthTokens(authTokens.accessToken, authTokens.refreshToken)
    }

    fun getAutTokens(): AuthTokens? {
        return sharedPrefs.getAuthTokens()
    }

    fun refreshTokenExist(): String? {
        return sharedPrefs.getAuthTokens()?.refreshToken
    }

    fun logout() {
        getAuthVkRepository()?.update(VKAuthResult.Logout)
        sharedPrefs.putAuthTokens("", "")
        sharedPrefs.putAuthMethod(null)
        sharedPrefs.setNotFirstOpenApp(false)
        sharedPrefs.putPastAuth(false)
        _logoutState.value = true
    }

    fun logoutStateOff() {
        _logoutState.value = false
    }
}