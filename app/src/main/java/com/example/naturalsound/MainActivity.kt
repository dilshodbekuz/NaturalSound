package com.example.naturalsound

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.naturalsound.service.MyForegroundService
import com.example.naturalsound.sound_play.Player
import com.example.naturalsound.sound_play.PlayerImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            MainView(viewModel)
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)
    }

    override fun onStop() {
        super.onStop()
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }


    //    override fun onPause() {
//        super.onPause()
//        val serviceIntent = Intent(this, MyForegroundService::class.java)
//        ContextCompat.startForegroundService(this, serviceIntent)
//    }
//
    override fun onDestroy() {
        super.onDestroy()
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)
    }
}



