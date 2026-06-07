class AndroidPlatform : Platform {
    override val name: Platform.Name = Platform.Name.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()