package com.example.naturalsound.sound_play

import android.media.MediaPlayer

interface Player {
    fun playSound(mediaPlayer: MediaPlayer)
    fun stopSound(mediaPlayer: MediaPlayer)
    fun resetSound()
}