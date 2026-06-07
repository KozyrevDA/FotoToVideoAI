import platform.UIKit.UIApplication
import platform.UIKit.endEditing

actual fun hideKeyboard(activity: Any?) {
    runCatching {
        UIApplication.sharedApplication.keyWindow?.endEditing(true)
    }
}