package ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.network.videos.dto.GenerateVideoResponse
import data.repository.AppRepository
import getVideoUriByName
import getVideos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import saveVideoToGallery
import showToast
import utils.managers.SubscriptionManager
import utils.tokens.TokensManager

class ResultVideoViewModel(
    private val appRepository: AppRepository,
    private val subscriptionManager: SubscriptionManager,
    private val tokensManager: TokensManager,
) : ViewModel() {
    private val _videosDownloaded = MutableStateFlow<List<String>>(emptyList())
    val videosDownloaded = _videosDownloaded.asStateFlow()

    private val _shareVideoPath = MutableStateFlow<String?>(null)
    val shareVideoPath = _shareVideoPath.asStateFlow()

    private val _downloading = MutableStateFlow<Set<String>>(emptySet())

    val isInstalledFromRuStore = subscriptionManager.isInstalledFromRuStore()
    val accessToken get() = tokensManager.getAutTokens()?.accessToken

    fun deleteVideo(idVideo: String) {
        viewModelScope.launch {
            appRepository.deleteVideo(idVideo)
        }
    }

    fun updateDownloadedVideos() {
        _videosDownloaded.value = getVideos().keys.map { it.substringBeforeLast(".") }
    }

    fun downloadVideo(
        idVideo: String,
        toastString: String,
        withShare: Boolean = false,
    ) {
        val existingUri = getVideoUriByName(idVideo)

        if (existingUri != null) {
            if (withShare) {
                _shareVideoPath.value = existingUri
            }
            return
        }

        if (idVideo in _downloading.value) return

        showToast(toastString)

        viewModelScope.launch {
            _downloading.update { it + idVideo }

            try {
                when (val response = appRepository.downloadVideo(idVideo)) {
                    is GenerateVideoResponse.Success -> {
                        response.video?.let { bytes ->
                            withContext(Dispatchers.IO) {
                                saveVideoToGallery(
                                    name = idVideo,
                                    videoBytes = bytes
                                )
                            }

                            _videosDownloaded.update { it + idVideo }

                            if (withShare) {
                                _shareVideoPath.value =
                                    getVideoUriByName(idVideo)
                            }
                        }
                    }

                    else -> Unit
                }
            } finally {
                _downloading.update { it - idVideo }
            }
        }
    }

    fun shareVideo(
        idVideo: String,
        toastString: String,
    ) {
        val videoExistPath = getVideoUriByName(idVideo)

        if (videoExistPath != null) {
            _shareVideoPath.value = videoExistPath
        } else {
            downloadVideo(
                idVideo = idVideo,
                toastString = toastString,
                withShare = true
            )
        }
    }
}