package com.naturalsound.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.naturalsound.domain.model.Sound
import com.naturalsound.ui.home.HomeScreen
import com.naturalsound.ui.mixer.MixerScreen
import com.naturalsound.ui.profile.ProfileScreen
import com.naturalsound.ui.timer.TimerScreen
import com.naturalsound.ui.theme.*

sealed class Screen(val route: String, val label: String, val emoji: String) {
    data object Home    : Screen("home",    "Asosiy", "🏠")
    data object Mixer   : Screen("mixer",   "Mixer",  "🎛️")
    data object Timer   : Screen("timer",   "Taymer", "⏱️")
    data object Profile : Screen("profile", "Profil", "👤")
    companion object { val all = listOf(Home, Mixer, Timer, Profile) }
}

@Composable
fun AppNavigation(
    activeSoundIds:   Set<String>,
    activeSoundNames: List<String>,
    currentVolumes:   Map<String, Float> = emptyMap(),
    onToggleSound:    (Sound) -> Unit,
    onStopSound:      (String) -> Unit,
    onPauseAll:       () -> Unit,
    onResumeAll:      () -> Unit,
    onStopAll:        () -> Unit,
    onSetVolume:      (String, Float) -> Unit,
    onFadeOutAndStop: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = BgDeep,
        bottomBar = { NsBottomBar(navController) }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(padding),
            enterTransition  = { fadeIn(tween(200)) + slideInVertically { it / 30 } },
            exitTransition   = { fadeOut(tween(160)) },
            popEnterTransition  = { fadeIn(tween(200)) },
            popExitTransition   = { fadeOut(tween(160)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateMixer  = { navController.navigate(Screen.Mixer.route) },
                    activeSoundIds   = activeSoundIds,
                    activeSoundNames = activeSoundNames,
                    onToggleSound    = onToggleSound,
                    onPauseAll       = onPauseAll
                )
            }
            composable(Screen.Mixer.route) {
                MixerScreen(
                    activeSoundIds = activeSoundIds,
                    currentVolumes = currentVolumes,
                    onStopSound    = onStopSound,
                    onSetVolume    = onSetVolume,
                    onPauseAll     = onPauseAll,
                    onResumeAll    = onResumeAll,
                    onStopAll      = onStopAll,
                    onAddSound     = { navController.navigate(Screen.Home.route) }
                )
            }
            composable(Screen.Timer.route) {
                TimerScreen(
                    activeCount      = activeSoundIds.size,
                    onFadeOutAndStop = onFadeOutAndStop
                )
            }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}

@Composable
private fun NsBottomBar(navController: androidx.navigation.NavHostController) {
    val stack by navController.currentBackStackEntryAsState()
    val current = stack?.destination
    NavigationBar(containerColor = Color(0xFF0E1220), tonalElevation = 0.dp) {
        Screen.all.forEach { screen ->
            val selected = current?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                selected = selected,
                onClick  = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true; restoreState = true
                    }
                },
                icon  = { Text(screen.emoji, fontSize = if (selected) 22.sp else 20.sp) },
                label = { Text(screen.label, fontSize = 10.sp, color = if (selected) Indigo else TextMuted) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Indigo,
                    unselectedIconColor = TextMuted,
                    indicatorColor      = IndigoBg
                )
            )
        }
    }
}
