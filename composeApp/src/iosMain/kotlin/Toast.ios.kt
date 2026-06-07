import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication

actual fun showToast(message: String) {
    val alert = UIAlertController.alertControllerWithTitle(
        title = null,
        message = message,
        preferredStyle = UIAlertControllerStyleAlert
    )
    alert.addAction(UIAlertAction.actionWithTitle("OK", UIAlertActionStyleDefault, null))
    val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootController?.presentViewController(alert, animated = true, completion = null)
}