package com.example.naturalsound.sound_play

import android.content.Context
import android.media.MediaPlayer
import com.example.naturalsound.SoundState

interface Player {
    fun playSound(mediaPlayer: MediaPlayer)
    fun stopSound(mediaPlayer: MediaPlayer)
    fun resetSound()
}