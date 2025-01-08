package com.example.naturalsound.sound_play

import android.media.MediaPlayer

class PlayerImpl : Player {
    private val mediaPlayers: MutableList<MediaPlayer> = mutableListOf()


    override fun playSound(mediaPlayer: MediaPlayer) {
        mediaPlayers.add(mediaPlayer)
    }

    override fun stopSound(mediaPlayer: MediaPlayer) {
        mediaPlayers.remove(mediaPlayer)
    }

    override fun resetSound() {
        mediaPlayers.forEach { mediaPlayer ->
            mediaPlayer.reset()
            mediaPlayer.stop() // Ovozlarni to'xtatish
            mediaPlayer.release() // MediaPlayerni ozod qilish
        }
        mediaPlayers.clear()
    }
}