import android.widget.Toast
import org.nla.phototovideoai.app.AndroidApp

actual fun showToast(message: String) = with(AndroidApp.INSTANCE) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}