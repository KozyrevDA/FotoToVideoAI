class IOSPlatformDeviceUid : PlatformDeviceUid {
    override val uid: String
        get() = generateUUID()
}

actual fun getPlatformDeviceUid(): PlatformDeviceUid = IOSPlatformDeviceUid()