package com.example.naturalsound.main

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturalsound.R
import com.example.naturalsound.sound_play.Player
import com.example.naturalsound.sound_play.PlayerImpl
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val player: Player by lazy { PlayerImpl() }
    private var mediaPlayerList = HashMap<Int, MediaPlayer>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    var timerJob: Job? = null


    private val sounds = persistentListOf(
        SoundModel(1, "Typing", R.raw.typing, R.drawable.typing),
        SoundModel(2, "Rain", R.raw.rain, R.drawable.rain),
        SoundModel(3, "Calm Rain", R.raw.calm_rain, R.drawable.calm_rain),
        SoundModel(4, "Storm and Rain", R.raw.storm_and_rain, R.drawable.storm_rain),
        SoundModel(5, "Water Full", R.raw.water_full, R.drawable.waterfall),
        SoundModel(6, "Calm River", R.raw.calm_river, R.drawable.calm_river),
        SoundModel(7, "Mountain River", R.raw.calm_mountain_river, R.drawable.mountain_river),
        SoundModel(8, "Ocean", R.raw.ocean, R.drawable.ocean),
        SoundModel(9, "Drip", R.raw.drip, R.drawable.drip),
        SoundModel(10, "Fire", R.raw.fire, R.drawable.fire),
        SoundModel(11, "Storm", R.raw.storm, R.drawable.storm),
        SoundModel(12, "Thunder", R.raw.long_thunder, R.drawable.long_thunder),
        SoundModel(13, "Peals of Thunder", R.raw.peals_of_thunder, R.drawable.peals_of_thunder),
        SoundModel(14, "Rain and Thunder", R.raw.rain_mix_thunder, R.drawable.rain_and_thunder),
        SoundModel(15, "Rain Thunder Wind", R.raw.thunder_rain_wind_mixed, R.drawable.rain_thunder_wind_mixed),
        SoundModel(16, "Wind", R.raw.wind, R.drawable.normal_wind),
        SoundModel(17, "Strong Wind", R.raw.strong_wild_wind, R.drawable.strong_wind),
        SoundModel(18, "Calm Forest", R.raw.calm_forest, R.drawable.calm_forest),
        SoundModel(19, "Jungle", R.raw.jungle, R.drawable.jungle),
        SoundModel(20, "Orphean", R.raw.eastern_orphean_bird, R.drawable.eastern_orphean),
        SoundModel(21, "Erithacus Rebecula", R.raw.erithacus_rebecula_bird, R.drawable.robin_bird),
        SoundModel(22, "Bird", R.raw.bird_pure_sound, R.drawable.pure_bird),
        SoundModel(23, "Greater Hoopoe", R.raw.greater_hoopoe_bird, R.drawable.greater_hoopoe),
        SoundModel(24, "Hermit Thrush", R.raw.hermit_thrush_bird, R.drawable.hermit_thrush_bird),
        SoundModel(25, "Indian Malabar", R.raw.indian_malabar_bird, R.drawable.indian_malabar_bird),
        SoundModel(26, "Birds and Frogs", R.raw.birds_vs_frog, R.drawable.frog_and_bords),
        SoundModel(27, "Olive Whistler", R.raw.olive_whistler_bird, R.drawable.olive_whistler),
        SoundModel(28, "Pied Butcherbird", R.raw.pied_butcherbird, R.drawable.pied_butcherbird),
        SoundModel(29, "Woodlark", R.raw.woodlark_bird, R.drawable.woodlark_bird),
        SoundModel(30, "Birds", R.raw.birds, R.drawable.birds),
    )

    init {
        _uiState.update { it.copy(sounds = sounds) }
    }

    private fun playSound(context: Context, item: SoundModel) {
        viewModelScope.launch {
            val selectList = mutableListOf<SoundModel>()
            selectList.addAll(uiState.value.selectList)
            selectList.add(item)
            _uiState.update { it.copy(selectList = selectList.toImmutableList()) }
            val mediaPlayer = MediaPlayer.create(context, item.sound).apply {
                isLooping = true
                start()
            }
            player.playSound(mediaPlayer = mediaPlayer)
            mediaPlayerList[item.id] = mediaPlayer
        }
    }

    private fun stopSound(item: SoundModel) {
        viewModelScope.launch {
            val selectList = mutableListOf<SoundModel>()
            selectList.addAll(uiState.value.selectList)
            selectList.remove(item)
            _uiState.update { it.copy(selectList = selectList.toImmutableList()) }
            val mediaPlayer = mediaPlayerList[item.id]
            mediaPlayer?.stop()
            player.stopSound(mediaPlayer!!)
        }
    }

    fun resetSound() {
        timerJob?.cancel()
        timerJob = null
        player.resetSound()
        _uiState.update { it.copy(selectList = persistentListOf(), counter = 0, progress = 1f) }
    }

    fun setTimer(timer: Int) {
        _uiState.update { it.copy(progress = 1f, percentage = 100) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            if (uiState.value.counter == 0) {
                _uiState.update { it.copy(counter = timer) }
            }
            var stepTime = timer * 60
            val a = timer * 60
            while (stepTime >= 1) {
                stepTime--
                _uiState.update { it.copy(counter = stepTime) }
                delay(1000)
                _uiState.update {
                    it.copy(
                        progress = stepTime.toFloat() / (timer * 60).toFloat(),
                        percentage = stepTime / a
                    )
                }
            }
            if (stepTime.toInt() < 1) {
                resetSound()
            }
        }
    }

    fun onClickButton(item: SoundModel, context: Context) {
        if (uiState.value.selectList.contains(item)) {
            stopSound(item)
        } else playSound(context, item)
    }

    fun setSheetContent(sheetContentType: BottomSheetContentType) {
        viewModelScope.launch {
            _uiState.update { it.copy(sheetContentTypes = sheetContentType) }
        }
    }
}

@Immutable
data class UiState(
    val sounds: ImmutableList<SoundModel> = persistentListOf(),
    val selectList: ImmutableList<SoundModel> = persistentListOf(),
    val counter: Int = 0,
    val progress: Float = 1f,
    val percentage: Int = 0,
    val sheetContentTypes: BottomSheetContentType = BottomSheetContentType.Times
)

enum class BottomSheetContentType {
    Times, Progress
}