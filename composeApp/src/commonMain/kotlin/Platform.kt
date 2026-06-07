interface Platform {
    val name: Name

    enum class Name {
        ANDROID, IOS
    }
}

expect fun getPlatform(): Platform