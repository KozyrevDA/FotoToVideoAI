import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import data.prefs.ACCESS_TOKEN
import data.prefs.IS_PAST_AUTH
import data.prefs.NOT_FIRST_OPEN_APP
import data.prefs.REFRESH_TOKEN
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.nla.phototovideoai.app.AndroidApp
import java.io.ByteArrayOutputStream
import java.io.File

const val SHARED_PREFS_NAME = "shared_prefs_app"
private const val PATH_IMAGES = "images"
private const val PATH_PHOTOS = "photos"
private const val TYPE_PHOTOS = "png"
private const val NOTIFICATION_CHANNELS_EXIST = "notification_channels_exist"

class AndroidSharedPreferences(private val appContext: Context) : SharedPreferences {
    override fun putInt(key: String, value: Int) {
        getSP().edit().putInt(key, value).apply()
    }

    override fun getInt(key: String): Int {
        return getSP().getInt(key, 0)
    }

    override fun putString(key: String, value: String) {
        getSP().edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return getSP().getString(key, null)
    }

    override fun putBool(key: String, value: Boolean) {
        getSP().edit().putBoolean(key, value).apply()
    }

    override fun getBool(key: String): Boolean {
        return getSP().getBoolean(key, false)
    }

    override fun getBool(key: String, defValue: Boolean): Boolean {
        return getSP().getBoolean(key, defValue)
    }

    override fun savePhoto(bitmap: ImageBitmap?, name: String): Boolean {
        return try {
            val photosDir = File(appContext.filesDir, "$PATH_IMAGES/$PATH_PHOTOS")

            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }

            val file = File(photosDir, "$name.$TYPE_PHOTOS")

            if (bitmap == null) {
                if (file.exists()) {
                    file.delete()
                } else false
            } else {
                file.outputStream().use { outputStream ->
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.asAndroidBitmap()
                        .compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    outputStream.write(byteArray)
                }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun getPhoto(name: String): Flow<ImageBitmap?> = callbackFlow {
        try {
            val photosDir = File(appContext.filesDir, "$PATH_IMAGES/$PATH_PHOTOS")
            val file = File(photosDir, "$name.$TYPE_PHOTOS")

            val bitmap = file.inputStream().use { inputStream ->
                val bytes = inputStream.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap.asImageBitmap()
            }

            trySend(bitmap)
        } catch (e: Exception) {
            trySend(null)
        }

        awaitClose()
    }

    fun putAuthTokens(accessToken: String, refreshToken: String) {
        getSP().edit().putString(ACCESS_TOKEN, accessToken).apply()
        getSP().edit().putString(REFRESH_TOKEN, refreshToken).apply()
    }

    fun putNotificationChannelsExist(value: Boolean) {
        getSP().edit().putBoolean(NOTIFICATION_CHANNELS_EXIST, value).apply()
    }

    fun getNotificationChannelsExist(): Boolean {
        return getSP().getBoolean(NOTIFICATION_CHANNELS_EXIST, false)
    }

    fun setNotFirstOpenApp(value: Boolean) {
        getSP().edit().putBoolean(NOT_FIRST_OPEN_APP, value).apply()
    }

    fun putPastAuth(value: Boolean) {
        getSP().edit().putBoolean(IS_PAST_AUTH, value).apply()
    }

    private fun getSP() = appContext.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
}

actual fun getSharedPreferences(): SharedPreferences = AndroidSharedPreferences(AndroidApp.INSTANCE)