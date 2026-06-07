import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import org.nla.phototovideoai.app.AndroidApp

class AndroidPlatformDeviceUid(private val appContext: Context) : PlatformDeviceUid {
    override val uid: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
}

actual fun getPlatformDeviceUid(): PlatformDeviceUid = AndroidPlatformDeviceUid(AndroidApp.INSTANCE)