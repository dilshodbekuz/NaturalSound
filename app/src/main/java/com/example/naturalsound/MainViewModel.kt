package com.example.naturalsound

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.lifecycle.AndroidViewModel
import com.example.naturalsound.sound_play.Player
import com.example.naturalsound.sound_play.PlayerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val player: Player by lazy { PlayerImpl() }

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private var mediaPlayerList = HashMap<Int, MediaPlayer>()
    val selectList = mutableListOf<SoundState?>()



    val sounds = listOf(
        SoundState(1, "Rain", R.raw.rain),
        SoundState(2, "Fire", R.raw.fire),
        SoundState(3, "Typing", R.raw.typing),
        SoundState(4, "Birds", R.raw.birds),
        SoundState(5, "Ocean", R.raw.ocean),
        SoundState(6, "Water Full", R.raw.water_full),
        SoundState(7, "Drip", R.raw.drip),
        SoundState(8, "Storm", R.raw.storm)
    )
    val animalsSound = listOf(
        SoundState(100, "Cat", R.raw.cat),
        SoundState(100, "Caw", R.raw.caw),
        SoundState(100, "Dog", R.raw.dog),
        SoundState(100, "Donkey", R.raw.donkey),
        SoundState(100, "Dogs", R.raw.dogs),
        SoundState(100, "Geese", R.raw.geese),
        SoundState(100, "Horse", R.raw.horse),
        SoundState(100, "Lion", R.raw.lion),
        SoundState(100, "Rooster", R.raw.rooster),
        SoundState(100, "Run horse", R.raw.run_horse),
        SoundState(100, "Wolf", R.raw.wolf),
    )

    fun playSound(item: SoundState?) {
        selectList.addAll(uiState.value.selectList)
        selectList.add(item)
        _uiState.update { it.copy(selectList = selectList) }
        val mediaPlayer = MediaPlayer.create(context, item?.sound ?: 0).apply {
            isLooping = true
            start()
        }
        player.playSound(mediaPlayer = mediaPlayer)
        mediaPlayerList[item!!.id!!] = mediaPlayer
    }

    fun stopSound(item: SoundState?) {
        selectList.addAll(uiState.value.selectList)
        selectList.remove(item)
        _uiState.update { it.copy(selectList = selectList) }
        val mediaPlayer = mediaPlayerList[item?.id!!]
        mediaPlayer?.stop()
        player.stopSound(mediaPlayer!!)
    }

    fun resetSound() {
        _uiState.update { it.copy(selectList = mutableListOf()) }
        player.resetSound()
    }
}

data class UiState(
    val selectList: MutableList<SoundState?> = mutableListOf(),
)