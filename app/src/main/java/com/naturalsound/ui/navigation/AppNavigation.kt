package com.naturalsound.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.naturalsound.domain.model.Sound
import com.naturalsound.ui.AppViewModel
import com.naturalsound.ui.home.HomeScreen
import com.naturalsound.ui.mixer.MixerScreen
import com.naturalsound.ui.onboarding.LanguageScreen
import com.naturalsound.ui.onboarding.NameScreen
import com.naturalsound.ui.onboarding.OnboardingViewModel
import com.naturalsound.ui.profile.ProfileScreen
import com.naturalsound.ui.timer.TimerScreen
import com.naturalsound.ui.theme.*

private const val ROUTE_LANGUAGE = "language"
private const val ROUTE_NAME     = "name"

sealed class Screen(val route: String, val emoji: String) {
    data object Home    : Screen("home",    "🏠")
    data object Mixer   : Screen("mixer",   "🎛️")
    data object Timer   : Screen("timer",   "⏱️")
    data object Profile : Screen("profile", "👤")
    companion object { val all = listOf(Home, Mixer, Timer, Profile) }
}

private val mainRoutes = Screen.all.map { it.route }.toSet()

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
    val onboardingVm: OnboardingViewModel = hiltViewModel()
    val appVm: AppViewModel = hiltViewModel()
    val lang  by appVm.lang.collectAsState()
    val theme by appVm.theme.collectAsState()
    val strings   = stringsFor(lang)
    val appColors = if (theme == "light") lightAppColors else darkAppColors

    val startDest = if (onboardingVm.isOnboardingDone()) Screen.Home.route else ROUTE_LANGUAGE

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    CompositionLocalProvider(
        LocalStrings   provides strings,
        LocalAppColors provides appColors
    ) {
        Scaffold(
            containerColor = BgDeep,
            bottomBar = {
                if (currentRoute in mainRoutes) NsBottomBar(navController)
            }
        ) { padding ->
            NavHost(
                navController    = navController,
                startDestination = startDest,
                modifier         = Modifier.padding(padding),
                enterTransition  = {
                    when (targetState.destination.route) {
                        ROUTE_LANGUAGE, ROUTE_NAME ->
                            fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 5 }
                        else ->
                            fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 25 }
                    }
                },
                exitTransition      = { fadeOut(tween(160)) },
                popEnterTransition  = { fadeIn(tween(200)) },
                popExitTransition   = { fadeOut(tween(160)) }
            ) {
                // ── Onboarding ───────────────────────────────────────────────
                composable(ROUTE_LANGUAGE) {
                    LanguageScreen(
                        onNext             = { navController.navigate(ROUTE_NAME) },
                        onLanguageSelected = appVm::setLanguage,
                        onThemeChange      = appVm::setTheme
                    )
                }
                composable(ROUTE_NAME) {
                    NameScreen(
                        onDone = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(ROUTE_LANGUAGE) { inclusive = true }
                            }
                        }
                    )
                }

                // ── Asosiy ekranlar ──────────────────────────────────────────
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
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onLanguageChange = appVm::setLanguage,
                        onThemeChange    = appVm::setTheme,
                        onLogout         = {
                            appVm.setLanguage("uz")
                            appVm.setTheme("dark")
                            navController.navigate(ROUTE_LANGUAGE) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NsBottomBar(navController: androidx.navigation.NavHostController) {
    val strings = LocalStrings.current
    val stack by navController.currentBackStackEntryAsState()
    val current = stack?.destination
    val labels = mapOf(
        Screen.Home.route    to strings.navHome,
        Screen.Mixer.route   to strings.navMixer,
        Screen.Timer.route   to strings.navTimer,
        Screen.Profile.route to strings.navProfile,
    )
    val c = LocalAppColors.current
    NavigationBar(containerColor = c.bgCard, tonalElevation = 0.dp) {
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
                label = { Text(labels[screen.route] ?: "", fontSize = 10.sp, color = if (selected) Indigo else c.textMuted) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Indigo,
                    unselectedIconColor = c.textMuted,
                    indicatorColor      = c.indigoBg
                )
            )
        }
    }
}
