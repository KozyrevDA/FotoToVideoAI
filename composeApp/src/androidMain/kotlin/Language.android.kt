import org.nla.phototovideoai.app.AndroidApp

actual fun getLanguage(): Language {
    return when (getCurrentLanguage()) {
        "ru", "be", "kk", "uk" -> Language.RU
        "pt" -> Language.PT
        else -> Language.EN
    }
}

private fun getCurrentLanguage(): String {
    return AndroidApp.INSTANCE.resources.configuration.locales[0].language
}