@file:OptIn(ExperimentalForeignApi::class)

import app.Constants.URL_APP_APPSTORE
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithBytes
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

class IOSShareProvider : ShareProvider {
    override fun shareToTelegram(bytes: ByteArray, caption: String?) {
        val fileUrl = saveTempFile(bytes) ?: return
        val encodedText = caption?.urlEncode() ?: ""
        val telegramUrl = NSURL(string = "tg://msg?text=$encodedText")
        if (UIApplication.sharedApplication.canOpenURL(telegramUrl)) {
            UIApplication.sharedApplication.openURL(telegramUrl)
        } else {
            presentShareSheet(fileUrl, caption)
        }
    }

    override fun shareToWhatsApp(bytes: ByteArray, caption: String?) {
        val fileUrl = saveTempFile(bytes) ?: return
        val encodedText = caption?.urlEncode() ?: ""
        val whatsappUrl = NSURL(string = "whatsapp://send?text=$encodedText")
        if (UIApplication.sharedApplication.canOpenURL(whatsappUrl)) {
            UIApplication.sharedApplication.openURL(whatsappUrl)
        } else {
            presentShareSheet(fileUrl, caption)
        }
    }

    override fun shareToInstagram(bytes: ByteArray, caption: String?) {
        val fileUrl = saveTempFile(bytes) ?: return
        val instagramUrl = NSURL(string = "instagram://app")
        if (UIApplication.sharedApplication.canOpenURL(instagramUrl)) {
            presentShareSheet(fileUrl, caption)
        } else {
            presentShareSheet(fileUrl, caption)
        }
    }

    override fun shareToVK(bytes: ByteArray, caption: String?) {
        val fileUrl = saveTempFile(bytes) ?: return
        val vkUrl = NSURL(string = "vk://app")
        if (UIApplication.sharedApplication.canOpenURL(vkUrl)) {
            UIApplication.sharedApplication.openURL(vkUrl)
        } else {
            presentShareSheet(fileUrl, caption)
        }
    }

    override fun shareToMax(bytes: ByteArray, caption: String?) {
        val fileUrl = saveTempFile(bytes) ?: return
        presentShareSheet(fileUrl, caption)
    }

    override fun shareApp() {
        val text = when (getLanguage()) {
            Language.RU, Language.BE, Language.KK, Language.UK -> "Я рекомендую приложение Оживление фото $URL_APP_APPSTORE"
            Language.PT -> "Eu recomendo o aplicativo Оживление фото $URL_APP_APPSTORE"
            Language.EN -> "I recommend the Оживление фото $URL_APP_APPSTORE"
        }
        val image = UIImage.imageNamed("AppIcon")

        val items = mutableListOf<Any>(text)
        image?.let { items.add(it) }

        val activityVC = UIActivityViewController(items, null)

        dispatch_async(dispatch_get_main_queue()) {
            val rootVC = getCurrentRootViewController()
            val topVC = getTopViewController(rootVC)
            if (topVC != null) {
                topVC.presentViewController(activityVC, animated = true, completion = null)
            } else {
                println("⚠️ Не удалось получить rootViewController для отображения share sheet.")
            }
        }
    }

    override fun readBytesFromUri(uri: String): ByteArray? = null

    private fun saveTempFile(bytes: ByteArray): NSURL? {
        val tmpDir = NSTemporaryDirectory() ?: return null
        val fileName = "shared_${NSDate().timeIntervalSince1970}.png"
        val filePath = tmpDir + fileName

        val nsData = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }

        nsData.writeToFile(filePath, atomically = true)
        return NSURL.fileURLWithPath(filePath)
    }

    private fun presentShareSheet(fileUrl: NSURL, caption: String?) {
        val items = mutableListOf<Any>(fileUrl)
        caption?.let { items.add(it) }

        val activityVC = UIActivityViewController(items, null)

        dispatch_async(dispatch_get_main_queue()) {
            val rootVC = getCurrentRootViewController()
            val topVC = getTopViewController(rootVC)
            topVC?.presentViewController(activityVC, animated = true, completion = null)
        }
    }

    /**
     * Получение корневого UIViewController
     */
    private fun getCurrentRootViewController(): UIViewController? {
        return UIApplication.sharedApplication.keyWindow?.rootViewController
    }

    /** Рекурсивно находит top-most VC */
    private fun getTopViewController(vc: UIViewController?): UIViewController? {
        return when {
            vc == null -> null
            vc.presentedViewController != null -> getTopViewController(vc.presentedViewController)
            vc is UINavigationController -> getTopViewController(vc.visibleViewController)
            vc is UITabBarController -> getTopViewController(vc.selectedViewController)
            else -> vc
        }
    }
}

/** URL-encode для текста */
private fun String.urlEncode(): String {
    val bytes = this.encodeToByteArray()
    val sb = StringBuilder(bytes.size * 3)
    for (b in bytes) {
        val c = b.toInt() and 0xFF
        val isUnreserved =
            (c in 0x30..0x39) || // 0-9
                    (c in 0x41..0x5A) || // A-Z
                    (c in 0x61..0x7A) || // a-z
                    c == 0x2D || c == 0x2E || c == 0x5F || c == 0x7E // -._~
        if (isUnreserved) {
            sb.append(c.toChar())
        } else {
            sb.append('%')
            val hex = c.toString(16).uppercase()
            if (hex.length == 1) sb.append('0')
            sb.append(hex)
        }
    }
    return sb.toString()
}

actual fun getShareProvider(): ShareProvider? = IOSShareProvider()