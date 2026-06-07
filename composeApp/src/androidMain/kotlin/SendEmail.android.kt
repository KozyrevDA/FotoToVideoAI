import android.content.Intent
import android.net.Uri
import android.os.Build
import org.nla.phototovideoai.BuildConfig
import org.nla.phototovideoai.app.AndroidApp

actual fun sendEmailToDefaultApp(body: String) = with(AndroidApp.INSTANCE) {
    val text = """$body 
            |
            |________________
            |Version: ${BuildConfig.VERSION_NAME}
            |Device: ${Build.MODEL}
            |Android OS ${Build.VERSION.RELEASE}
        """.trimMargin()
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("NewLevelApp@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Запрос функции")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooserIntent = Intent.createChooser(intent, "Отправить через").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(chooserIntent)
}