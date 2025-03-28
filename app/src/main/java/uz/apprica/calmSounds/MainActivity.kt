package uz.apprica.calmSounds

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.apprica.calmSounds.main.MainView
import uz.apprica.calmSounds.main.MainViewModel
import uz.apprica.calmSounds.service.MyForegroundService
import uz.apprica.calmSounds.splash.SplashScreen
import uz.apprica.calmSounds.ui.theme.NaturalSoundTheme
import uz.apprica.calmSounds.utils.Constants
import uz.apprica.calmSounds.utils.LanguageHelper
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    //    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        manageAppLanguage()
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
                onResult = {}
            )
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
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
                        MainView(
                            viewModel = viewModel,
                            onClickBack = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.freeprivacypolicy.com/live/ccfd6657-014c-4718-ba6b-b7825c6588de")
                                )
                                context.startActivity(intent)
                            }
                        )
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

//    private fun manageAppLanguage() {
//        preferences = this.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
//        val code = preferences.getString(Constants.LANGUAGE_KEY, Constants.Languages.ENG)
//        code?.let {
//            LanguageHelper.changeLanguage(this, resources, it)
//        }
//    }
}



