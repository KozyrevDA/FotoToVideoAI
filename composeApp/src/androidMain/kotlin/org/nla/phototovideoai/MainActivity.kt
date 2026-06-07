package org.nla.phototovideoai

import AndroidSharedPreferences
import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import app.App
import com.msilimon.vkauthdonate.VKAuthActivity
import com.msilimon.vkauthdonate.VkApi
import com.msilimon.vkauthdonate.data.VKAuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rustore.sdk.pay.IntentInteractor
import ru.rustore.sdk.pay.RuStorePayClient
import ui.theme.MidnightShadow

class MainActivity : FragmentActivity() {
    private val intentInteractor: IntentInteractor by lazy {
        RuStorePayClient.instance.getIntentInteractor()
    }

    private val authResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                VKAuthActivity.RESULT_AUTH -> {
                    VkApi.setPressedAuthVkButton(true)
                    val sharedPreferences = AndroidSharedPreferences(this@MainActivity)
                    sharedPreferences.setNotFirstOpenApp(true)
                    sharedPreferences.putPastAuth(true)
                }

                VKAuthActivity.RESULT_NOT_AUTH -> {
                    VkApi.setPressedAuthVkButton(false)
                }
            }
        }
    private var redirectToDonut = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(MidnightShadow.toArgb()),
            navigationBarStyle = SystemBarStyle.dark(MidnightShadow.toArgb())
        )
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            intentInteractor.proceedIntent(intent)
        }

        setContent {
            KeepScreenOn()
            App(
                activity = this,
                onAuthVK = ::initAuthVk,
            )
        }

        checkAuthVk()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intentInteractor.proceedIntent(intent)
    }

    private fun initAuthVk() {
        if (VkApi.isPressedAuthVkButton()) {
            lifecycleScope.launch(Dispatchers.IO) {
                when (val result = VkApi.auth(this@MainActivity)) {
                    is VKAuthResult.LoggedIn -> {
                        VkApi.logout()
                        VkApi.update(VkApi.auth(this@MainActivity))
                    }

                    is VKAuthResult.Authorized -> {
                        VkApi.update(result)
                    }

                    VKAuthResult.Unauthorized -> {
                        VkApi.setPressedAuthVkButton(false)
                        VkApi.setSubVk(false)
                    }

                    VKAuthResult.Logout -> {}
                }
            }
        } else {
            authResult.launch(Intent(this, VKAuthActivity::class.java))
        }
    }

    private fun checkAuthVk() {
        if (VkApi.isPressedAuthVkButton()) {
            lifecycleScope.launch(Dispatchers.IO) {
                when (val result = VkApi.auth(null)) {
                    is VKAuthResult.LoggedIn, is VKAuthResult.Authorized -> {
                        VkApi.update(result)
                    }

                    VKAuthResult.Unauthorized -> {
                        VkApi.setPressedAuthVkButton(false)
                        VkApi.setSubVk(false)
                        VkApi.update(VKAuthResult.Unauthorized)
                    }

                    VKAuthResult.Logout -> {
                        VkApi.update(VKAuthResult.Logout)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}