interface ShareProvider {
    fun shareToTelegram(bytes: ByteArray, caption: String?)
    fun shareToWhatsApp(bytes: ByteArray, caption: String?)
    fun shareToInstagram(bytes: ByteArray, caption: String?)
    fun shareToMax(bytes: ByteArray, caption: String?)
    fun shareToVK(bytes: ByteArray, caption: String?)
    fun shareApp()
    fun readBytesFromUri(uri: String): ByteArray?
}

expect fun getShareProvider(): ShareProvider?