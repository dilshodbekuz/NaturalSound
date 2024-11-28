package com.example.naturalsound.sound_play

import android.content.Context
import android.media.MediaPlayer
import com.example.naturalsound.MainViewModel.SoundState

class PlayerImpl : Player {
    private val mediaPlayers: MutableList<MediaPlayer> = mutableListOf()


    override fun playSound(context: Context, sounds: List<SoundState?>) {
        sounds.forEach {
            val mediaPlayer = MediaPlayer.create(context, it?.sound ?: 0).apply {
                isLooping = true
                start()
            }
            mediaPlayers.add(mediaPlayer)
        }
    }

    override fun stopSound(context: Context, music: Int?) {
        music?.let { MediaPlayer.create(context, it).stop() }
    }

    override fun resetSound() {
        mediaPlayers.forEach { mediaPlayer ->
            mediaPlayer.stop() // Ovozlarni to'xtatish
            mediaPlayer.release() // MediaPlayerni ozod qilish
        }
        mediaPlayers.clear()
    }
}