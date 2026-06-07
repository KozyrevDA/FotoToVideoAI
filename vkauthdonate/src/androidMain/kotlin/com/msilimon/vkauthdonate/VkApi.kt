package com.msilimon.vkauthdonate

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.msilimon.vkauthdonate.data.AuthVkRepository
import com.msilimon.vkauthdonate.data.VKAuthResult
import com.msilimon.vkauthdonate.prefs.SharedPreferences
import com.msilimon.vkauthdonate.prefs.SharedPreferencesImpl
import com.msilimon.vkauthdonate.repository.AndroidAuthVkRepository
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import com.vk.dto.common.id.UserId
import com.vk.id.AccessToken
import com.vk.id.VKID
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthCallback
import com.vk.id.auth.VKIDAuthParams
import com.vk.id.logout.VKIDLogoutCallback
import com.vk.id.logout.VKIDLogoutFail
import com.vk.id.vksdksupport.withVKIDToken
import com.vk.sdk.api.account.AccountService
import com.vk.sdk.api.account.dto.AccountUserSettings
import com.vk.sdk.api.base.dto.BaseBoolInt
import com.vk.sdk.api.donut.DonutService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object VkApi : SharedPreferences, AuthVkRepository {
    private const val TAG = "VkApi"
    private const val URL_DONATE_MOBILE =
        "https://m.vk.ru/app7132546#/payment?source=description&owner_id=-"

    private var groupIdVk: String = ""
    private val tokenTracker = object : VKTokenExpiredHandler {
        override fun onTokenExpired() {
            sharedPreferences?.setPressedAuthVkButton(false)
        }
    }
    private var sharedPreferences: SharedPreferences? = null
    private val androidAuthVkRepository = AndroidAuthVkRepository()

    override val vkAuthResultFlow: StateFlow<VKAuthResult?>
        get() = androidAuthVkRepository.vkAuthResultFlow
    override val vkSubFlow: StateFlow<Boolean>
        get() = androidAuthVkRepository.vkSubFlow

    override fun setPressedAuthVkButton(value: Boolean) {
        sharedPreferences?.setPressedAuthVkButton(value)
    }

    override fun isPressedAuthVkButton(): Boolean {
        return sharedPreferences?.isPressedAuthVkButton() ?: false
    }

    override fun setSubVk(value: Boolean) {
        sharedPreferences?.setSubVk(value)
    }

    override fun getSubVk(): Boolean {
        return sharedPreferences?.getSubVk() ?: false
    }

    override fun putIdVk(value: String) {
        sharedPreferences?.putIdVk(value)
    }

    override fun getIdVk(): String? {
        return sharedPreferences?.getIdVk()
    }

    override fun update(vkAuthResult: VKAuthResult) {
        androidAuthVkRepository.update(vkAuthResult)
    }

    override fun update(isVkSubEnabled: Boolean) {
        androidAuthVkRepository.update(isVkSubEnabled)
    }

    fun initializeApp(
        appContext: Context,
        groupIdVk: String,
        sharedPrefsName: String? = null
    ) {
        this.groupIdVk = groupIdVk
        sharedPreferences = SharedPreferencesImpl(
            context = appContext,
            sharedPrefsName = sharedPrefsName
        )
        VKID.init(appContext)
        VK.addTokenExpiredHandler(tokenTracker)
    }

    fun isDonGroup(action: (result: Boolean) -> Unit) {
        if (isLoggedInVKID()) {
            isDonGroupNew {
                action(it)
            }
        } else {
            isDonGroupOld {
                action(it)
            }
        }
    }

    suspend fun logout() {
        VKID.instance.logout(
            callback = object : VKIDLogoutCallback {
                override fun onFail(fail: VKIDLogoutFail) {
                }

                override fun onSuccess() {
                }
            }
        )

        VK.logout()
    }

    fun redirectToDonut(activity: FragmentActivity) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(URL_DONATE_MOBILE.plus(groupIdVk))
            )
        )
    }

    suspend fun auth(activity: FragmentActivity?): VKAuthResult {
        return suspendCoroutine { continuation ->
            if (VKID.instance.accessToken != null) {
                continuation.resume(VKAuthResult.LoggedIn(idVk = VKID.instance.accessToken!!.userID.toString()))
            } else if (VK.isLoggedIn()) {
                continuation.resume(VKAuthResult.LoggedIn(idVk = VK.getUserId().value.toString()))
            } else {
                activity?.let {
                    it.lifecycleScope.launch {
                        continuation.resume(authNew(it))
                    }
                } ?: continuation.resume(VKAuthResult.Unauthorized)
            }
        }
    }

    @Deprecated("use authNew from VKID lib")
    private suspend fun authOld(activity: FragmentActivity): VKAuthResult {
        return suspendCoroutine { continuation ->
            try {
                val authLauncher = VK.login(activity) { result: VKAuthenticationResult ->
                    when (result) {
                        is VKAuthenticationResult.Success -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                continuation.resume(
                                    VKAuthResult.Authorized(
                                        email = result.token.email,
                                        fullName = getFullName(),
                                        accessToken = result.token.accessToken,
                                        idVk = ""
                                    )
                                )
                            }
                        }

                        is VKAuthenticationResult.Failed -> {
                            continuation.resume(VKAuthResult.Unauthorized)
                        }
                    }
                }

                authLauncher.launch(
                    arrayListOf(
                        VKScope.GROUPS,
                        VKScope.OFFLINE,
                        VKScope.EMAIL
                    )
                )
            } catch (e: Exception) {
                continuation.resume(VKAuthResult.Unauthorized)
            }
        }
    }

    private suspend fun authNew(activity: FragmentActivity): VKAuthResult {
        return suspendCoroutine { continuation ->
            val vkidAuthCallback = object : VKIDAuthCallback {
                override fun onAuth(accessToken: AccessToken) {
                    val result = VKAuthResult.Authorized(
                        email = accessToken.userData.email,
                        fullName = "${accessToken.userData.firstName} ${accessToken.userData.lastName}",
                        accessToken = accessToken.token,
                        idVk = accessToken.userID.toString()
                    )
                    continuation.resume(result)
                }

                override fun onFail(fail: VKIDAuthFail) {
                    when (fail) {
                        is VKIDAuthFail.Canceled -> {
                            continuation.resume(VKAuthResult.Unauthorized)
                        }

                        else -> {
                            continuation.resume(VKAuthResult.Unauthorized)
                        }
                    }
                }
            }

            VKID.instance.authorize(
                lifecycleOwner = activity,
                callback = vkidAuthCallback,
                params = VKIDAuthParams { scopes = setOf("groups", "email", "offline") }
            )
        }
    }

    private suspend fun getFullName(): String? {
        return suspendCoroutine { continuation ->
            VK.execute(
                AccountService().accountGetProfileInfo(),
                object : VKApiCallback<AccountUserSettings> {
                    override fun success(result: AccountUserSettings) {
                        continuation.resume("${result.firstName} ${result.lastName}")
                    }

                    override fun fail(error: Exception) {
                        continuation.resume(null)
                    }
                }
            )
        }
    }

    @Deprecated("use isDonGroupNew from VKID lib")
    private fun isDonGroupOld(action: (result: Boolean) -> Unit) {
        VK.execute(
            DonutService().donutIsDon(UserId("-$groupIdVk".toLong())),
            object : VKApiCallback<BaseBoolInt> {
                override fun success(result: BaseBoolInt) {
                    when (result.value) {
                        0 -> action(false)
                        1 -> action(true)
                    }
                }

                override fun fail(error: Exception) {
                    action(false)
                }
            }
        )
    }

    private fun isDonGroupNew(action: (result: Boolean) -> Unit) {
        VK.execute(
            DonutService().donutIsDon(UserId("-$groupIdVk".toLong())).withVKIDToken(),
            object : VKApiCallback<BaseBoolInt> {
                override fun success(result: BaseBoolInt) {
                    when (result.value) {
                        0 -> action(false)
                        1 -> action(true)
                    }
                }

                override fun fail(error: Exception) {
                    action(false)
                }
            }
        )
    }

    private fun isLoggedInVKID() = VKID.instance.accessToken != null
}