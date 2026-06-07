package org.nla.phototovideoai

import AndroidSharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RedirectHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.data?.let { handleRedirect(it) }
        finish()
    }

    private fun handleRedirect(uri: Uri) {
        val accessToken = uri.getQueryParameter("accessToken")
        val refreshToken = uri.getQueryParameter("refreshToken")

        if (accessToken != null && refreshToken != null) {
            saveTokens(accessToken, refreshToken)
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        val sharedPreferences = AndroidSharedPreferences(this)
        sharedPreferences.putAuthTokens(accessToken, refreshToken)
    }
}