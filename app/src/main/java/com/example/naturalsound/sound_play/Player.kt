package com.example.naturalsound.sound_play

import android.content.Context
import com.example.naturalsound.MainViewModel.SoundState

interface Player {
    fun playSound(context: Context, sounds: List<SoundState?>)
    fun stopSound(context: Context, music: Int?)
    fun resetSound()
}