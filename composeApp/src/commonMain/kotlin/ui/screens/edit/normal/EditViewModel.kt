package ui.screens.edit.normal

import Language
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.model.ChatMessage
import data.model.QueueGen
import data.model.QueueGenStatus
import data.model.SystemPromt
import data.model.Template
import data.network.videos.dto.GenerateVideoResponse
import data.prefs.IMAGE_PICK_PHOTO_1
import data.prefs.IMAGE_PICK_PHOTO_2
import data.prefs.SharedPrefs
import data.repository.AppRepository
import getLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import utils.managers.SubscriptionManager
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeSource

private val safetyStatuses = setOf(
    QueueGenStatus.ERROR_SAFETY_SYSTEM_SEX_CONTENT,
    QueueGenStatus.TERMS_OF_SERVICE,
    QueueGenStatus.ERROR_SAFETY_SYSTEM_HUMAN,
    QueueGenStatus.MINOR_CHILDREN,
    QueueGenStatus.FAILED,
    QueueGenStatus.TIMEOUT
)

class EditViewModel(
    private val appRepository: AppRepository,
    private val sharedPrefs: SharedPrefs,
    private val nameFilter: String,
    private val restore: Boolean,
    private val subscriptionManager: SubscriptionManager,
) : ViewModel() {
    private var updateGenerateQueueJob: Job? = null

    private val _photo1 = MutableStateFlow<ImageBitmap?>(null)
    val photo1 = _photo1.asStateFlow()

    private val _photo2 = MutableStateFlow<ImageBitmap?>(null)
    val photo2 = _photo2.asStateFlow()

    private val _prompt = MutableStateFlow<String?>(null)
    val prompt = _prompt.asStateFlow()

    private val _waitResult = MutableStateFlow(false)
    val waitResult = _waitResult.asStateFlow()

    private val _notEnoughCoins = MutableStateFlow(false)
    val notEnoughCoins = _notEnoughCoins.asStateFlow()

    private val _errorSafetySystem = MutableStateFlow(false)
    val errorSafetySystem = _errorSafetySystem.asStateFlow()

    private val _templatesMap = MutableStateFlow<Map<String, List<Template>>>(emptyMap())
    val templatesMap = _templatesMap.asStateFlow()

    private val _generationQueue = MutableStateFlow<List<QueueGen>>(emptyList())
    val generationQueue = _generationQueue.asStateFlow()

    private val _generatedIdVideoEvent = MutableSharedFlow<String>(replay = 0)
    val generatedIdVideoEvent: SharedFlow<String> = _generatedIdVideoEvent

    val isPastAuth get() = sharedPrefs.isPastAuth()
    val isInstalledFromRuStore = subscriptionManager.isInstalledFromRuStore()

    init {
        viewModelScope.launch {
            if (restore) {
                sharedPrefs.getPhoto(IMAGE_PICK_PHOTO_1).firstOrNull()?.let {
                    _photo1.value = it
                }
                sharedPrefs.getPhoto(IMAGE_PICK_PHOTO_2).firstOrNull()?.let {
                    _photo2.value = it
                }
                sharedPrefs.getLastPrompt()?.let {
                    _prompt.value = it
                }
            } else {
                sharedPrefs.savePhoto(null, IMAGE_PICK_PHOTO_1)
                sharedPrefs.savePhoto(null, IMAGE_PICK_PHOTO_2)
                sharedPrefs.putLastPrompt("")
            }
            updateFiltersList()
        }
    }

    fun generateVideo(currentTemplatePath: String?) {
        viewModelScope.launch {
            if (nameFilter != "videoFromPrompt") {
                if (nameFilter == "beforeAfter") {
                    _photo1.firstOrNull() ?: return@launch
                    _photo2.firstOrNull() ?: return@launch
                } else {
                    _photo1.firstOrNull() ?: return@launch
                }
            }

            val chatMessage = ChatMessage(
                userPrompt = _prompt.value,
                systemPromt = SystemPromt(
                    nameFilter = nameFilter,
                    secondTemplatePath = currentTemplatePath
                        ?.takeUnless { it.contains(nameFilter, ignoreCase = true) }
                ),
                isUser = true,
                language = getLanguage(),
                timestamp = Clock.System.now()
            )

            _waitResult.value = true

            when (
                appRepository.generateVideo(
                    chatMessage = chatMessage,
                    photo1 = _photo1.value,
                    photo2 = _photo2.value
                )
            ) {
                GenerateVideoResponse.NotEnoughCoins -> _notEnoughCoins.value = true
                else -> {}
            }

            _waitResult.value = false
        }
    }

    fun savePhoto1(bitmap: ImageBitmap?) {
        sharedPrefs.savePhoto(
            bitmap = bitmap,
            name = IMAGE_PICK_PHOTO_1
        )
        sharedPrefs.putUidLastFilter(nameFilter)
        _photo1.value = bitmap
    }

    fun savePhoto2(bitmap: ImageBitmap?) {
        sharedPrefs.savePhoto(
            bitmap = bitmap,
            name = IMAGE_PICK_PHOTO_2
        )
        sharedPrefs.putUidLastFilter(nameFilter)
        _photo2.value = bitmap
    }

    fun savePrompt(prompt: String) {
        _prompt.value = prompt
        sharedPrefs.putLastPrompt(prompt)
    }

    fun hideNotEnoughCoins() {
        _notEnoughCoins.value = false
    }

    fun hideErrorSafetySystem() {
        _errorSafetySystem.value = false
    }

    fun updateGenerateQueue(forced: Boolean = false) {
        if (updateGenerateQueueJob != null && forced) {
            updateGenerateQueueJob?.cancel()
            updateGenerateQueueJob = null
        } else if (updateGenerateQueueJob != null) {
            return
        }

        updateGenerateQueueJob = viewModelScope.launch {
            try {
                val startMark = TimeSource.Monotonic.markNow()
                val maxDuration = 12.minutes

                var oldList: List<QueueGen> = sharedPrefs.getQueueUids()
                    .mapNotNull { uid -> appRepository.getStatusVideo(uid) }

                while (isActive) {
                    val newList = appRepository.getGenerateQueue(QueueGenStatus.GENERATION)

                    _generationQueue.value = newList
                    sharedPrefs.putQueueUids(newList)

                    val oldUids = oldList.map { it.uid }.toSet()
                    val newUids = newList.map { it.uid }.toSet()
                    val removedUids = oldUids - newUids
                    val processedList = mutableListOf<QueueGen>()

                    removedUids.forEach { uid ->
                        try {
                            val status = appRepository.getStatusVideo(uid)
                            if (status != null) {
                                handleQueueGenStatus(status)
                            } else {
                                oldList.find { it.uid == uid }?.let(processedList::add)
                            }
                        } catch (_: Exception) {
                            oldList.find { it.uid == uid }?.let(processedList::add)
                        }
                    }

                    oldList = newList + processedList.filter { it.uid !in newUids }

                    if ((newList.isEmpty() && !forced) || startMark.elapsedNow() >= maxDuration) {
                        break
                    }

                    delay(2_000)
                }
            } finally {
                updateGenerateQueueJob = null
            }
        }
    }

    private suspend fun handleQueueGenStatus(queueGen: QueueGen) {
        when (queueGen.status) {
            in safetyStatuses -> {
                _errorSafetySystem.value = true
            }

            QueueGenStatus.COMPLETED -> {
                queueGen.idVideo?.let { _generatedIdVideoEvent.emit(it) }
            }

            else -> {}
        }
    }

    private suspend fun updateFiltersList() {
        _templatesMap.value = appRepository.getAllTemplatesMP4().groupBy {
            when (getLanguage()) {
                Language.RU, Language.BE, Language.KK, Language.UK -> it.groupRu
                Language.PT -> it.groupPt
                Language.EN -> it.groupEn
            }
        }
    }
}