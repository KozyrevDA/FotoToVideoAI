class AndroidSdkVersion : SdkVersion {
    override val number: Int = android.os.Build.VERSION.SDK_INT
}

actual fun getSdkVersion(): SdkVersion? = AndroidSdkVersion()