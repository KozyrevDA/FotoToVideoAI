import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import app.Constants.URL_APP_GOOGLE_PLAY
import app.Constants.URL_APP_RUSTORE
import org.nla.phototovideoai.R
import org.nla.phototovideoai.app.AndroidApp
import org.nla.phototovideoai.utils.CheckInstalled
import java.io.File
import java.io.FileOutputStream

class AndroidShareProvider(private val appContext: Context) : ShareProvider {
    override fun shareToTelegram(bytes: ByteArray, caption: String?) {
        shareVideo("org.telegram.messenger", bytes, caption)
    }

    override fun shareToWhatsApp(bytes: ByteArray, caption: String?) {
        shareVideo("com.whatsapp", bytes, caption)
    }

    override fun shareToInstagram(bytes: ByteArray, caption: String?) {
        shareVideo("com.instagram.android", bytes, caption)
    }

    override fun shareToMax(bytes: ByteArray, caption: String?) {
        shareVideo("ru.oneme.app", bytes, caption)
    }

    override fun shareToVK(bytes: ByteArray, caption: String?) {
        shareVideo("com.vkontakte.android", bytes, caption)
    }

    override fun shareApp() = with(appContext) {
        val drawable = AppCompatResources.getDrawable(this, R.drawable.ic_launcher)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "share_app_icon.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        val storeLink = getString(R.string.recommend_app).run {
            if (CheckInstalled.isInstalledFromRuStore()) {
                plus(" $URL_APP_RUSTORE")
            } else {
                plus(" $URL_APP_GOOGLE_PLAY")
            }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            setType("image/png")
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app))
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, storeLink)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(
            Intent.createChooser(intent, getString(R.string.share_via)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }


    override fun readBytesFromUri(uri: String): ByteArray? {
        return try {
            AndroidApp.INSTANCE.contentResolver.openInputStream(uri.toUri())?.use { it.readBytes() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun shareVideo(packageName: String, bytes: ByteArray, caption: String?) {
        try {
            val file = File(appContext.cacheDir, "shared_${System.currentTimeMillis()}.mp4")
            file.writeBytes(bytes)

            val uri = FileProvider.getUriForFile(
                appContext,
                "${appContext.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, caption)
                setPackage(packageName)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            appContext.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(
                appContext,
                appContext.getString(R.string.app_not_found),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}

actual fun getShareProvider(): ShareProvider? = AndroidShareProvider(AndroidApp.INSTANCE)