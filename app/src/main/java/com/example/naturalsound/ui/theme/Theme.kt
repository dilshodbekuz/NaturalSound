package com.example.naturalsound.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFFF2F2F7)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFF2F2F7)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun NaturalSoundTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    colors: NaturalSoundColors = ThemeColors.getColors(darkTheme),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColors provides colors,
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primaryContainer = colors.background
            ),
            content = content
        )
    }
//
//
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
}

val LocalColors = staticCompositionLocalOf<NaturalSoundColors> {
    error("No LocalColors specified")
}

object AppColors {
    val color: NaturalSoundColors
        @Composable get() = LocalColors.current
}

@Immutable
data class NaturalSoundColors(
    val background: Color,
    val textColor: Color,
    val selectedColor: Color,
    val darkColor: Color
)

object ThemeColors {
    fun getColors(darTheme: Boolean): NaturalSoundColors {
        return if (darTheme) themeDark else themeLight
    }

    private val themeDark = NaturalSoundColors(
        background = Color(0xFF2F2F38),
        textColor = Color(0xFFFFFFFF),
        selectedColor = Color(0xFF4DE8E8),
        darkColor = Color(0xFF000000)
    )
    private val themeLight = NaturalSoundColors(
        background = Color(0xFFF2F2F7),
        textColor = Color(0xFF000000),
        selectedColor = Color(0xFF4DE8E8),
        darkColor = Color(0xFFFFFFFF)
    )
}