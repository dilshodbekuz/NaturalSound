package com.example.naturalsound

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
import com.example.naturalsound.utils.Constants
import com.example.naturalsound.utils.LanguageHelper

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        manageAppLanguage()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                0
//            )
//        }
        setContent {
            val context = LocalContext.current
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
//                        sendNotification(context)
                    } else {
                        // Handle permission denial
                    }
                }
            )
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    // If the permission is already granted, send a notification
//                    sendNotification(context)
                }
            }
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
                        MainView(viewModel, onClickBack = { })
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

    override fun onPause() {
        super.onPause()
        if (viewModel.uiState.value.selectList.isNotEmpty()) {
            val serviceIntent = Intent(this, MyForegroundService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }
//    override fun onStop() {
//        super.onStop()
//        if (viewModel.uiState.value.selectList.isNotEmpty()) {
//            val serviceIntent = Intent(this, MyForegroundService::class.java)
//            ContextCompat.startForegroundService(this, serviceIntent)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetSound()
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        stopService(serviceIntent)
    }

    private fun manageAppLanguage() {
        preferences = this.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val code = preferences.getString(Constants.LANGUAGE_KEY, Constants.Languages.ENG)
        code?.let {
            LanguageHelper.changeLanguage(this, resources, it)
        }
    }
}



