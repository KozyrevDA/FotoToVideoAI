import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getLanguage(): Language {
    return when (getCurrentLanguage()) {
        "ru", "be", "kk", "uk" -> Language.RU
        "pt" -> Language.PT
        else -> Language.EN
    }
}

private fun getCurrentLanguage(): String {
    return NSLocale.currentLocale.languageCode
}