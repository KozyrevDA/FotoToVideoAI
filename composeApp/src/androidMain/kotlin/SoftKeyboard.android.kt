import android.app.Activity
import android.view.inputmethod.InputMethodManager

actual fun hideKeyboard(activity: Any?) {
    runCatching {
        if (activity is Activity) {
            val imm = activity.getSystemService(InputMethodManager::class.java)
            val view = activity.currentFocus ?: activity.window?.decorView
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }
}