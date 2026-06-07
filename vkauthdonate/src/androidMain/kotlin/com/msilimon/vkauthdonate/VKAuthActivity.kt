package com.msilimon.vkauthdonate

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.msilimon.vkauthdonate.data.VKAuthResult
import kotlinx.coroutines.launch

class VKAuthActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth()
    }

    private fun auth() {
        lifecycleScope.launch {
            when (val result = VkApi.auth(this@VKAuthActivity)) {
                is VKAuthResult.LoggedIn -> {
                    setResult(RESULT_AUTH)
                    finish()
                }

                is VKAuthResult.Authorized -> {
                    VkApi.update(result)
                    setResult(RESULT_AUTH)
                    finish()
                }

                VKAuthResult.Unauthorized -> {
                    setResult(RESULT_NOT_AUTH)
                    finish()
                }

                VKAuthResult.Logout -> {}
            }
        }
    }

    companion object {
        const val RESULT_AUTH = 10101
        const val RESULT_NOT_AUTH = 10103
    }
}