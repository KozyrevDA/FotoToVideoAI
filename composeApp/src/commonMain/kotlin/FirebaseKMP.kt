interface FirebaseKMP {
    fun initialize()
    fun reportEvent(eventName: String)
    fun reportEvent(eventName: String, parameters: Map<String, Any>)
    fun recordException(throwable: Throwable)
}

expect fun getFirebaseKMP(): FirebaseKMP