interface PlatformDeviceUid {
    val uid: String
}

expect fun getPlatformDeviceUid(): PlatformDeviceUid