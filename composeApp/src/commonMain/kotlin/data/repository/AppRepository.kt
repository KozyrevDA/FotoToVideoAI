package data.repository

import SignInCredential
import androidx.compose.ui.graphics.ImageBitmap
import com.msilimon.vkauthdonate.data.VKAuthResult
import com.revenuecat.purchases.kmp.models.CustomerInfo
import data.local.Database
import data.model.AuthMethod
import data.model.AuthResponse
import data.model.ChatMessage
import data.model.QueueGen
import data.model.QueueGenStatus
import data.model.Template
import data.model.Video
import data.model.billing.google.PurchaseGoogle
import data.model.toDto
import data.model.toEntity
import data.network.SpaceXApi
import data.network.billing.confirmPurchaseGoogle
import data.network.billing.confirmPurchaseRevCat
import data.network.billing.dto.RevCatPurchaseDTO
import data.network.config.getMonthSubCoins
import data.network.config.getPriceVkDonutMonthRub
import data.network.config.getShowMoreTokensButtonWhenNonSub
import data.network.config.getShowOnboarding
import data.network.config.getShowStartPaywall
import data.network.config.getShowTrialGeneration
import data.network.config.getStartCoins
import data.network.templates.dto.GetTemplates
import data.network.templates.dto.toModel
import data.network.templates.getAllTemplates
import data.network.templates.getAllTemplatesMP4
import data.network.user.authApple
import data.network.user.authVK
import data.network.user.authenticate
import data.network.user.deleteAccount
import data.network.user.dto.UserDto
import data.network.user.generationAvailable
import data.network.user.getUser
import data.network.user.logout
import data.network.user.signIn
import data.network.videos.deleteVideo
import data.network.videos.downloadVideo
import data.network.videos.dto.GenerateVideoResponse
import data.network.videos.dto.toModel
import data.network.videos.generateTrialVideo
import data.network.videos.generateVideo
import data.network.videos.generationIsProcessing
import data.network.videos.getAllInfoVideos
import data.network.videos.getGenerateQueue
import data.network.videos.getStatusVideo
import data.network.videos.uploadOriginImageAndPrompt
import data.prefs.SharedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppRepository(
    private val sharedPrefs: SharedPrefs,
    private val localBase: Database,
    private val remoteBase: SpaceXApi,
) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _countCoins = MutableStateFlow<Int?>(sharedPrefs.getLastCountCoins())
    val countCoins = _countCoins.asStateFlow()

    private val _isProSub = MutableStateFlow<Boolean>(sharedPrefs.getLastIsProSub())
    val isProSub = _isProSub.asStateFlow()

    private val refreshAvatarsRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        if (!sharedPrefs.isPastAuth()) {
            coroutineScope.launch {
                getStartCoins()
            }
        }
    }

    suspend fun generateVideo(
        chatMessage: ChatMessage,
        photo1: ImageBitmap?,
        photo2: ImageBitmap?,
    ): GenerateVideoResponse {
        val result = generationAvailable()
        if (!result) {
            remoteBase.uploadOriginImageAndPrompt(
                photo1 = photo1,
                photo2 = photo2,
                chatMessageDto = chatMessage.toDto()
            )
            return GenerateVideoResponse.NotEnoughCoins
        }
        return remoteBase.generateVideo(
            chatMessageDto = chatMessage.toDto(),
            photo1 = photo1,
            photo2 = photo2
        )
    }

    fun requestVideosRefresh() {
        refreshAvatarsRequests.tryEmit(Unit)
    }

    suspend fun authVK(authorized: VKAuthResult.Authorized): AuthResponse {
        val email = authorized.email ?: return AuthResponse.Error("email null")
        val fullName = authorized.fullName ?: return AuthResponse.Error("fullName null")
        val accessToken = authorized.accessToken ?: return AuthResponse.Error("token null")
        val idVk = authorized.idVk ?: return AuthResponse.Error("idVk null")

        return remoteBase.authVK(
            email = email,
            fullName = fullName,
            accessToken = accessToken,
            idVk = idVk
        )
    }

    suspend fun authApple(signInCredential: SignInCredential): AuthResponse {
        return when (signInCredential) {
            is SignInCredential.Apple -> {
                remoteBase.authApple(
                    email = signInCredential.email,
                    fullName = signInCredential.fullName,
                    accessToken = signInCredential.token,
                    idApple = signInCredential.id
                )
            }

            is SignInCredential.Error -> AuthResponse.Error(message = signInCredential.desc)
        }
    }

    suspend fun signIn(email: String, password: String): AuthResponse {
        val authResponse = remoteBase.signIn(email, password)
        if (authResponse is AuthResponse.Authorized) {
            sharedPrefs.putAuthMethod(AuthMethod.EMAIL)
        }
        return authResponse
    }

    suspend fun authenticate(): AuthResponse {
        val authResponse = remoteBase.authenticate()
        if (authResponse is AuthResponse.Authorized) {
            authResponse.authMethod?.let { sharedPrefs.putAuthMethod(it) }
        }
        return authResponse
    }

    fun getAuthMethod(): AuthMethod? {
        return sharedPrefs.getAuthMethod()
    }

    suspend fun getUser(): UserDto? {
        return remoteBase.getUser()?.also {
            sharedPrefs.putLastCountCoins(it.coins)
            _countCoins.value = it.coins
        }
    }

    suspend fun confirmPurchaseGoogle(purchaseGoogle: PurchaseGoogle): Boolean? {
        return remoteBase.confirmPurchaseGoogle(purchaseGoogle)
    }

    suspend fun confirmPurchaseRevCat(customerInfo: CustomerInfo): Boolean? {
        val revCatPurchasesList = customerInfo.entitlements.all
            .mapNotNull { (id, ent) ->
                RevCatPurchaseDTO(
                    appUserId = customerInfo.originalAppUserId,
                    productId = id,
                    purchaseDate = ent.latestPurchaseDateMillis,
                    store = ent.store.name
                )
            }

        return remoteBase.confirmPurchaseRevCat(revCatPurchasesList)
    }

    suspend fun getPriceVkDonutMonthRub(): Int? {
        return remoteBase.getPriceVkDonutMonthRub()
    }

    suspend fun getShowOnboarding(): Boolean {
        return if (sharedPrefs.isNotFirstOpenApp()) {
            val remoteConfigLocal = sharedPrefs.isShowOnboarding()

            coroutineScope.launch {
                val remoteConfigServer = remoteBase.getShowOnboarding()
                sharedPrefs.putShowOnboarding(remoteConfigServer)
            }

            remoteConfigLocal
        } else {
            val remoteConfigServer = remoteBase.getShowOnboarding()
            sharedPrefs.putShowOnboarding(remoteConfigServer)
            getStartCoins()
            getShowStartPaywall()
            getMonthSubCoins()
            remoteConfigServer
        }
    }

    suspend fun getShowStartPaywall(): Boolean {
        return if (sharedPrefs.isNotFirstOpenApp()) {
            val remoteConfigLocal = sharedPrefs.isShowStartPaywall()

            coroutineScope.launch {
                val remoteConfigServer = remoteBase.getShowStartPaywall()
                sharedPrefs.putShowStartPaywall(remoteConfigServer)
            }

            remoteConfigLocal
        } else {
            val remoteConfigServer = remoteBase.getShowStartPaywall()
            sharedPrefs.putShowStartPaywall(remoteConfigServer)
            remoteConfigServer
        }
    }

    suspend fun getStartCoins(): Int {
        return if (sharedPrefs.isNotFirstOpenApp()) {
            val remoteConfigLocal = sharedPrefs.getLastCountCoins()

            coroutineScope.launch {
                val remoteConfigServer = remoteBase.getStartCoins()
                sharedPrefs.putLastCountCoins(remoteConfigServer)
            }

            remoteConfigLocal
        } else {
            val startCoins = remoteBase.getStartCoins()
            sharedPrefs.putLastCountCoins(startCoins)
            _countCoins.value = startCoins
            startCoins
        }
    }

    fun putLastIsProSub(value: Boolean) {
        _isProSub.value = value
        sharedPrefs.putLastIsProSub(value = value)
    }

    suspend fun generationIsProcessing(): Boolean? {
        return remoteBase.generationIsProcessing()
    }

    suspend fun getAllTemplates(): List<Template> {
        return when (val getFilters = remoteBase.getAllTemplates()) {
            is GetTemplates.AllTemplates -> getFilters.templates.map { it.toModel() }
            else -> emptyList()
        }
    }

    suspend fun getAllTemplatesMP4(): List<Template> {
        return when (val getFilters = remoteBase.getAllTemplatesMP4()) {
            is GetTemplates.AllTemplates -> getFilters.templates.map { it.toModel() }
            else -> emptyList()
        }
    }

    fun logout() {
        remoteBase.logout()
    }

    suspend fun deleteAccount(): Boolean {
        remoteBase.deleteAccount()
        sharedPrefs.setPastOnboarding(false)
        remoteBase.logout()
        return true
    }

    suspend fun getMonthSubCoins(): Int? {
        return if (sharedPrefs.isNotFirstOpenApp()) {
            val remoteConfigLocal = sharedPrefs.getMonthSubCoins()

            coroutineScope.launch {
                val remoteConfigServer = remoteBase.getMonthSubCoins()
                sharedPrefs.putMonthSubCoins(remoteConfigServer)
            }

            remoteConfigLocal
        } else {
            val remoteConfigServer = remoteBase.getMonthSubCoins()
            sharedPrefs.putMonthSubCoins(remoteConfigServer)
            remoteConfigServer
        }
    }

    suspend fun getShowMoreTokensButtonWhenNonSub(): Boolean {
        return remoteBase.getShowMoreTokensButtonWhenNonSub()
    }

    suspend fun getAllInfoVideos(): List<Video> {
        val videos = remoteBase.getAllInfoVideos().map { it.toModel() }
        videos.forEach { localBase.insertOrUpdateVideo(it.toEntity()) }
        return videos
    }

    suspend fun deleteVideo(idVideo: String) {
        remoteBase.deleteVideo(idVideo)
    }

    suspend fun downloadVideo(idVideo: String): GenerateVideoResponse {
        return remoteBase.downloadVideo(idVideo)
    }

    suspend fun getStatusVideo(uid: String): QueueGen? {
        return remoteBase.getStatusVideo(uid)?.toModel()
    }

    suspend fun getGenerateQueue(status: QueueGenStatus): List<QueueGen> {
        return remoteBase.getGenerateQueue(status).map { it.toModel() }
    }

    suspend fun generateTrialVideo(photo1: ImageBitmap): GenerateVideoResponse {
        return remoteBase.generateTrialVideo(photo1 = photo1)
    }

    suspend fun getShowTrialGeneration(): Boolean {
        return if (sharedPrefs.isNotFirstOpenApp()) {
            val local = sharedPrefs.isShowTrialGeneration()

            coroutineScope.launch {
                val remote = remoteBase.getShowTrialGeneration()
                sharedPrefs.putShowTrialGeneration(remote)
            }

            local
        } else {
            val remote = remoteBase.getShowTrialGeneration()
            sharedPrefs.putShowTrialGeneration(remote)
            remote
        }
    }

    private suspend fun generationAvailable(): Boolean {
        return remoteBase.generationAvailable()
    }
}