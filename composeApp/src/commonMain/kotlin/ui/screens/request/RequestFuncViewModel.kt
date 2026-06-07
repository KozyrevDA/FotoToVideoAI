package ui.screens.request

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RequestFuncViewModel : ViewModel() {
    private val _textRequest = MutableStateFlow("")
    val textRequest = _textRequest.asStateFlow()

    fun onTextRequest(text: String) {
        _textRequest.value = text
    }
}