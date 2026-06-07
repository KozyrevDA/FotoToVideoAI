import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow

interface SharedPreferences {
    fun putInt(key: String, value: Int)
    fun getInt(key: String): Int
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun putBool(key: String, value: Boolean)
    fun getBool(key: String): Boolean
    fun getBool(key: String, defValue: Boolean): Boolean
    fun savePhoto(bitmap: ImageBitmap?, name: String): Boolean
    fun getPhoto(name: String): Flow<ImageBitmap?>
}

expect fun getSharedPreferences(): SharedPreferences