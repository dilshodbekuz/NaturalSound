package uz.apprica.calmSounds.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Composable
fun NaturalSoundTheme(
    darkTheme: Boolean = true /*isSystemInDarkTheme()*/,
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
    val darkColor: Color,
    val white: Color
)

object ThemeColors {
    fun getColors(darTheme: Boolean): NaturalSoundColors {
        return if (darTheme) themeDark else themeLight
    }

    private val themeDark = NaturalSoundColors(
        background = Color(0xFF2F2F38),
        textColor = Color(0xFFFFFFFF),
        white = Color(0xFFFFFFFF),
        selectedColor = Color(0xFF4DE8E8),
        darkColor = Color(0xFF000000)
    )
    private val themeLight = NaturalSoundColors(
        background = Color(0xFFF2F2F7),
        textColor = Color(0xFF000000),
        selectedColor = Color(0xFF4DE8E8),
        darkColor = Color(0xFFFFFFFF),
        white = Color(0xFFFFFFFF),
    )
}