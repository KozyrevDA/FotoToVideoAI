class IOSPlatform : Platform {
    override val name: Platform.Name = Platform.Name.IOS
}

actual fun getPlatform(): Platform = IOSPlatform()