interface ProviderAuthPage {
    fun launchAuthGoogle()
    suspend fun launchAuthApple(): SignInCredential
}

sealed interface SignInCredential {
    data class Apple(
        val token: String,
        val authCode: String,
        val id: String,
        val email: String?,
        val fullName: String?,
        val likelyRealPerson: Boolean,
    ) : SignInCredential

    data class Error(val desc: String? = null) : SignInCredential
}

expect fun getProviderAuthPage(): ProviderAuthPage