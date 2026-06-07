import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okio.FileSystem
import org.jetbrains.skia.Image
import platform.Foundation.NSUserDefaults

private const val PATH_IMAGES = "images"
private const val PATH_PHOTOS = "photos"
private const val TYPE_PHOTOS = "dat"

class IOSSharedPreferences : SharedPreferences {
    override fun putInt(key: String, value: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
    }

    override fun getInt(key: String): Int {
        return NSUserDefaults.standardUserDefaults.integerForKey(key).toInt()
    }

    override fun putString(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, key)
    }

    override fun getString(key: String): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key)
    }

    override fun putBool(key: String, value: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(value, key)
    }

    override fun getBool(key: String): Boolean {
        return NSUserDefaults.standardUserDefaults.boolForKey(key)
    }

    override fun getBool(key: String, defValue: Boolean): Boolean {
        val defaults = NSUserDefaults.standardUserDefaults
        return if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else {
            defValue
        }
    }

    override fun savePhoto(bitmap: ImageBitmap?, name: String): Boolean {
        return try {
            val directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.div(PATH_IMAGES).div(PATH_PHOTOS)

            if (!FileSystem.SYSTEM.exists(directory)) {
                FileSystem.SYSTEM.createDirectories(directory)
            }

            val file = directory.div("$name.$TYPE_PHOTOS")

            if (bitmap == null) {
                if (FileSystem.SYSTEM.exists(file)) {
                    FileSystem.SYSTEM.delete(file)
                }
                true
            } else {
                val image = Image.makeFromBitmap(bitmap.asSkiaBitmap())
                val byteArray = image.encodeToData()?.bytes ?: return false

                FileSystem.SYSTEM.write(file) {
                    write(byteArray)
                }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun getPhoto(name: String): Flow<ImageBitmap?> = callbackFlow {
        try {
            val directory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.div(PATH_IMAGES).div(PATH_PHOTOS)
            val file = directory.div("$name.$TYPE_PHOTOS")

            FileSystem.SYSTEM.read(file) {
                trySend(Image.makeFromEncoded(readByteArray()).toComposeImageBitmap())
            }
        } catch (e: Exception) {
            trySend(null)
        }

        awaitClose()
    }
}

actual fun getSharedPreferences(): SharedPreferences = IOSSharedPreferences()