package com.example.naturalsound

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.naturalsound.main.MainView
import com.example.naturalsound.main.MainViewModel
import com.example.naturalsound.service.MyForegroundService
import com.example.naturalsound.splash.SplashScreen
import com.example.naturalsound.ui.theme.NaturalSoundTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        setContent {
            viewModel = viewModel()
            val navController = rememberNavController()
            NaturalSoundTheme {
                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(navController)
                    }
                    composable("home") {
                        MainView(viewModel)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)
    }

    override fun onStop() {
        super.onStop()
        if (viewModel.uiState.value.selectList.isNotEmpty()) {
            val serviceIntent = Intent(this, MyForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetSound()
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)
    }
}



