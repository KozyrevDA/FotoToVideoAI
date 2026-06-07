package ui.components

import androidx.compose.runtime.mutableFloatStateOf

class Segment(val number: Int) {
    private var _progress = mutableFloatStateOf(0F)
    val progress = _progress

    fun start() {
        _progress.value = 1F
    }

    fun reset() {
        _progress.value = 0F
    }
}