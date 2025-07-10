package uz.apprica.calmSounds.home

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.apprica.calmSounds.sound_play.Player
import uz.apprica.calmSounds.sound_play.PlayerImpl

const val name = "name"
const val id = "id"
const val sound = "sound"
const val image = "image"

class MainScreenModel : ViewModel() {

    private val player: Player by lazy { PlayerImpl() }
    private var mediaPlayerList = HashMap<String, MediaPlayer>()

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    var timerJob: Job? = null
    var job: Job? = null

    init {
        getSounds()
    }

    private fun getSounds() {
        db.collection("sounds")
            .get()
            .addOnSuccessListener { result ->
                val productList = mutableListOf<SoundModel>()
                result.forEach { document ->
                    val a = SoundModel(
                        id = document.id,
                        value = document.getString(name) ?: "",
                        sound = document.getString(sound) ?: "",
                        image = document.getString(image) ?: ""
                    )
                    productList.add(a)
                    _uiState.update { it.copy(sounds = productList.toPersistentList()) }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun playSound(context: Context, item: SoundModel) {
        job?.cancel()
        job = null
        job = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.update { it.copy(selectList = uiState.value.selectList + item) }
                val mediaPlayer = MediaPlayer.create(context, item.sound.toUri()).apply {
                    isLooping = true
                    start()
                }
                try {
                    player.playSound(mediaPlayer = mediaPlayer)
                    mediaPlayerList[item.id] = mediaPlayer
                } catch (e: Exception) {
                    resetSound()
                }
            }
        }
    }

    private fun stopSound(item: SoundModel) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _uiState.update { it.copy(selectList = uiState.value.selectList - item) }
                try {
                    val mediaPlayer = mediaPlayerList[item.id]
                    mediaPlayer?.let { player.stopSound(it) }
                    mediaPlayer?.stop()
                } catch (e: IllegalStateException) {
                    resetSound()
                }
            }
        }
    }

    fun resetSound() {
        try {
            job?.cancel()
            job = null
            timerJob?.cancel()
            timerJob = null
            player.resetSound()
            _uiState.update {
                it.copy(
                    selectList = persistentListOf(),
                    counter = 0,
                    progress = 1f,
                    sheetContentTypes = BottomSheetContentType.Times
                )
            }
        } catch (e: IllegalStateException) {
            job?.cancel()
            job = null
            timerJob?.cancel()
            timerJob = null
        }
    }

    fun setTimer(timer: Int, isResound: Boolean = true) {
        _uiState.update { it.copy(progress = 1f) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            if (uiState.value.counter == 0) _uiState.update { it.copy(counter = timer) }
            var stepTime = timer * 60
            while (stepTime >= 1) {
                val minute = stepTime / 60
                val seconds = stepTime % 60
                stepTime--
                _uiState.update { it.copy(counter = stepTime, minAndSec = "$minute : $seconds") }
                delay(1000)
                _uiState.update {
                    it.copy(
                        progress = stepTime.toFloat() / (timer * 60).toFloat(),
                    )
                }
            }
            if (stepTime.toInt() < 1 && isResound) resetSound()
        }
    }

    fun onClickButton(item: SoundModel, context: Context) {
        viewModelScope.launch {
            if (uiState.value.selectList.contains(item)) {
                stopSound(item)
            } else playSound(context, item)
        }
    }

    fun setSheetContent(sheetContentType: BottomSheetContentType) {
        viewModelScope.launch {
            _uiState.update { it.copy(minAndSec = "", sheetContentTypes = sheetContentType) }
        }
    }
}

@Immutable
data class UiState(
    val sounds: ImmutableList<SoundModel> = persistentListOf(),
    val selectList: List<SoundModel> = persistentListOf(),
    val counter: Int = 0,
    val minAndSec: String = "",
    val progress: Float = 1f,
    val sheetContentTypes: BottomSheetContentType = BottomSheetContentType.Times,
    val menuVisibility: Boolean = false
)

enum class BottomSheetContentType {
    Times, Progress
}