package com.naturalsound.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Shared accent colors (bir xil ikkala temada) ─────────────────────────────
val Indigo        = Color(0xFF6366F1)
val IndigoLight   = Color(0xFFA5B4FC)
val GreenActive   = Color(0xFF4ADE80)
val PinkLike      = Color(0xFFF472B6)

// ── AppColors — tema bilan o'zgaradigan ranglar ───────────────────────────────
data class AppColors(
    val bgDeep:        Color,
    val bgCard:        Color,
    val bgElevated:    Color,
    val bgBorder:      Color,
    val textPrimary:   Color,
    val textSecondary: Color,
    val textMuted:     Color,
    val indigoBg:      Color,
    val isDark:        Boolean,
)

val darkAppColors = AppColors(
    bgDeep        = Color(0xFF0B0F1A),
    bgCard        = Color(0xFF131829),
    bgElevated    = Color(0xFF1A1F35),
    bgBorder      = Color(0xFF1E2A3A),
    textPrimary   = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF94A3B8),
    textMuted     = Color(0xFF475569),
    indigoBg      = Color(0xFF16183A),
    isDark        = true,
)

val lightAppColors = AppColors(
    bgDeep        = Color(0xFFF0F4F8),
    bgCard        = Color(0xFFFFFFFF),
    bgElevated    = Color(0xFFE8EFF7),
    bgBorder      = Color(0xFFCBD5E1),
    textPrimary   = Color(0xFF0F172A),
    textSecondary = Color(0xFF334155),
    textMuted     = Color(0xFF64748B),
    indigoBg      = Color(0xFFEEF2FF),
    isDark        = false,
)

val LocalAppColors = compositionLocalOf { darkAppColors }

// ── Backward-compat aliases (eskisi ishlayversin) ─────────────────────────────
val BgDeep        get() = darkAppColors.bgDeep
val BgCard        get() = darkAppColors.bgCard
val BgElevated    get() = darkAppColors.bgElevated
val BgBorder      get() = darkAppColors.bgBorder
val IndigoBg      get() = darkAppColors.indigoBg
val TextPrimary   get() = darkAppColors.textPrimary
val TextSecondary get() = darkAppColors.textSecondary
val TextMuted     get() = darkAppColors.textMuted

// ── Material color schemes ────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = Indigo,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFF16183A),
    onPrimaryContainer = IndigoLight,
    background         = Color(0xFF0B0F1A),
    onBackground       = Color(0xFFF1F5F9),
    surface            = Color(0xFF131829),
    onSurface          = Color(0xFFF1F5F9),
    surfaceVariant     = Color(0xFF1A1F35),
    onSurfaceVariant   = Color(0xFF94A3B8),
    outline            = Color(0xFF1E2A3A),
    secondary          = GreenActive,
    tertiary           = PinkLike,
    error              = Color(0xFFF87171)
)

private val LightColorScheme = lightColorScheme(
    primary            = Indigo,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF3730A3),
    background         = Color(0xFFF0F4F8),
    onBackground       = Color(0xFF0F172A),
    surface            = Color(0xFFFFFFFF),
    onSurface          = Color(0xFF0F172A),
    surfaceVariant     = Color(0xFFE8EFF7),
    onSurfaceVariant   = Color(0xFF334155),
    outline            = Color(0xFFCBD5E1),
    secondary          = Color(0xFF16A34A),
    tertiary           = Color(0xFFDB2777),
    error              = Color(0xFFDC2626)
)

@Composable
fun NaturalSoundTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = NsTypography,
        content     = content
    )
}
