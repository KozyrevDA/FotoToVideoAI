import android.content.Intent
import android.net.Uri
import org.nla.phototovideoai.app.AndroidApp

actual fun openUrl(url: String) {
    with(AndroidApp.INSTANCE) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}