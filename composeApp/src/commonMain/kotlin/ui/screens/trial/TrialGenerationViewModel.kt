package ui.screens.trial

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.network.videos.dto.GenerateVideoResponse
import data.prefs.SharedPrefs
import data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TrialGenerationState {
    object Idle : TrialGenerationState()
    object Loading : TrialGenerationState()
    object Success : TrialGenerationState()
    data class Error(val message: String?) : TrialGenerationState()
}

class TrialGenerationViewModel(
    private val appRepository: AppRepository,
    private val sharedPrefs: SharedPrefs,
) : ViewModel() {

    private val _state = MutableStateFlow<TrialGenerationState>(TrialGenerationState.Idle)
    val state = _state.asStateFlow()

    private val _selectedPhoto = MutableStateFlow<ImageBitmap?>(null)
    val selectedPhoto = _selectedPhoto.asStateFlow()

    fun setPhoto(bitmap: ImageBitmap) {
        _selectedPhoto.value = bitmap
    }

    fun generateTrial() {
        val photo = _selectedPhoto.value ?: return
        _state.value = TrialGenerationState.Loading

        viewModelScope.launch {
            when (val result = appRepository.generateTrialVideo(photo1 = photo)) {
                is GenerateVideoResponse.Success -> {
                    sharedPrefs.putTrialUsed(true)
                    _state.value = TrialGenerationState.Success
                }
                is GenerateVideoResponse.InternalServerError -> {
                    _state.value = TrialGenerationState.Error(result.message)
                }
                else -> {
                    _state.value = TrialGenerationState.Error(null)
                }
            }
        }
    }

    fun isTrialUsed(): Boolean = sharedPrefs.isTrialUsed()
}
