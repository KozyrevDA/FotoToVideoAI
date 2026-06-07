package ui.screens.auth

import SignInCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msilimon.vkauthdonate.data.VKAuthResult
import data.model.AuthResponse
import data.prefs.SharedPrefs
import data.repository.AppRepository
import getAuthVkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AuthViewModel(
    private val appRepository: AppRepository,
    private val sharedPrefs: SharedPrefs,
) : ViewModel() {
    private val authVkRepository = getAuthVkRepository()
    private var googleProviderJob: Job? = null
    private val authMethodFlow = MutableStateFlow(appRepository.getAuthMethod())

    private val _authResponseFlow = MutableStateFlow<AuthResponse?>(null)
    val authResponseFlow = _authResponseFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val isNotFirstOpenApp get() = sharedPrefs.isNotFirstOpenApp()

    init {
        if (authVkRepository != null) {
            viewModelScope.launch(Dispatchers.Default) {
                authVkRepository.vkAuthResultFlow.collect { result ->
                    when (result) {
                        is VKAuthResult.Authorized -> {
                            _authResponseFlow.value = appRepository.authVK(result)
                            authenticate()
                        }

                        is VKAuthResult.LoggedIn -> {
                            _isLoading.value = false
                            _authResponseFlow.value = AuthResponse.Authorized()
                        }

                        VKAuthResult.Unauthorized -> {
                            _isLoading.value = false
                            _authResponseFlow.value = AuthResponse.Unauthorized
                        }

                        VKAuthResult.Logout -> {
                            _isLoading.value = false
                        }

                        null -> {
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
    }

    fun authGoogleProvider() {
        googleProviderJob?.cancel()
        googleProviderJob = viewModelScope.launch(Dispatchers.Default) {
            repeat(60) {
                if (isActive) {
                    delay(1000L)
                    authenticate()
                } else {
                    _isLoading.value = false
                    cancel()
                }
            }
        }
    }

    fun setPassedAuth() {
        sharedPrefs.setNotFirstOpenApp(true)
        sharedPrefs.putPastAuth(true)
    }

    fun applyLoading() {
        _isLoading.value = true
    }

    suspend fun authApple(signInCredential: SignInCredential) {
        _authResponseFlow.value = appRepository.authApple(signInCredential)
        authenticate()
    }

    suspend fun getShowStartPaywall(): Boolean {
        return appRepository.getShowStartPaywall()
    }

    private suspend fun authenticate() {
        val authResponse = appRepository.authenticate()
        _authResponseFlow.value = authResponse

        when (authResponse) {
            is AuthResponse.Authorized -> {
                authMethodFlow.value = authResponse.authMethod
                //downloadAll()
                googleProviderJob?.cancel()
            }

            else -> {
                authMethodFlow.value = null
            }
        }
    }
}