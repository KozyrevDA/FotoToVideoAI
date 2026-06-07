package ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.Video
import data.network.videos.dto.GenerateVideoResponse
import data.repository.AppRepository
import getVideoUriByName
import getVideos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import saveVideoToGallery
import showToast
import utils.managers.SubscriptionManager
import utils.tokens.TokensManager

class HistoryViewModel(
    private val appRepository: AppRepository,
    private val subscriptionManager: SubscriptionManager,
    private val tokensManager: TokensManager,
) : ViewModel() {
    private var generationIsProcessingJob: Job? = null

    private val _videos = MutableStateFlow<List<Video>?>(null)
    val videos = _videos.asStateFlow()

    private val _countCoins = MutableStateFlow<Int?>(appRepository.countCoins.value)
    val countCoins = _countCoins.asStateFlow()

    private val _generationIsProcessingState = MutableStateFlow<Boolean?>(null)
    val generationIsProcessingState = _generationIsProcessingState.asStateFlow()

    private val _videosDownloaded = MutableStateFlow<List<String>>(emptyList())
    val videosDownloaded = _videosDownloaded.asStateFlow()

    val isProSub = appRepository.isProSub
    val accessToken get() = tokensManager.getAutTokens()?.accessToken

    init {
        updateCoins()
        updateProSub()
        generationIsProcessingCollect()
    }

    fun updateCoins() {
        viewModelScope.launch {
            val coins = appRepository.getUser()?.coins ?: return@launch
            _countCoins.value = coins
        }
    }

    fun generationIsProcessingCollect() {
        if (generationIsProcessingJob?.isActive != true) {
            generationIsProcessingJob = viewModelScope.launch {
                try {
                    while (isActive) {
                        _generationIsProcessingState.value = appRepository.generationIsProcessing()
                        appRepository.requestVideosRefresh()
                        updateCoins()
                        delay(15_000L)
                    }
                } finally {
                    generationIsProcessingJob = null
                }
            }
        }
    }

    fun updateProSub() {
        viewModelScope.launch {
            val result = subscriptionManager.isHaveSubPremium()
            appRepository.putLastIsProSub(result)
        }
    }

    fun updateVideos() {
        viewModelScope.launch {
            _videos.value = appRepository.getAllInfoVideos()
        }
    }

    fun deleteVideo(idVideo: String) {
        viewModelScope.launch {
            appRepository.deleteVideo(idVideo)
            updateVideos()
        }
    }

    fun updateDownloadedVideos() {
        _videosDownloaded.value = getVideos().keys.map { it.substringBeforeLast(".") }
    }

    fun downloadVideo(
        video: Video,
        toastString: String,
    ) {
        val id = video.idVideo
        val existingUri = getVideoUriByName(id)

        if (existingUri != null) return

        showToast(toastString)

        viewModelScope.launch {
            when (val response = appRepository.downloadVideo(idVideo = id)) {
                is GenerateVideoResponse.Success -> {
                    response.video?.let { bytes ->
                        withContext(Dispatchers.IO) {
                            saveVideoToGallery(
                                name = id,
                                videoBytes = bytes
                            )
                        }

                        _videosDownloaded.update { list ->
                            list + id
                        }
                    }
                }

                else -> Unit
            }
        }
    }

}