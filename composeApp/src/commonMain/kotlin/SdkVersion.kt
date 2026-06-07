interface SdkVersion {
    val number: Int
}

expect fun getSdkVersion(): SdkVersion?