import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import org.nla.phototovideoai.app.AndroidApp
import data.network.DEFAULT_IP

class AndroidProviderAuthPage : ProviderAuthPage {
    override fun launchAuthGoogle() {
        with(AndroidApp.INSTANCE) {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            val intent = customTabsIntent.intent.apply {
                data = Uri.parse(DEFAULT_IP.plus("users/signin/google"))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    override suspend fun launchAuthApple(): SignInCredential = SignInCredential.Error()
}

actual fun getProviderAuthPage(): ProviderAuthPage = AndroidProviderAuthPage()