package com.naturalsound.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BgDeep        = Color(0xFF0B0F1A)
val BgCard        = Color(0xFF131829)
val BgElevated    = Color(0xFF1A1F35)
val BgBorder      = Color(0xFF1E2A3A)
val Indigo        = Color(0xFF6366F1)
val IndigoLight   = Color(0xFFA5B4FC)
val IndigoBg      = Color(0xFF16183A)
val TextPrimary   = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted     = Color(0xFF475569)
val GreenActive   = Color(0xFF4ADE80)
val PinkLike      = Color(0xFFF472B6)

private val DarkColorScheme = darkColorScheme(
    primary            = Indigo,
    onPrimary          = Color.White,
    primaryContainer   = IndigoBg,
    onPrimaryContainer = IndigoLight,
    background         = BgDeep,
    onBackground       = TextPrimary,
    surface            = BgCard,
    onSurface          = TextPrimary,
    surfaceVariant     = BgElevated,
    onSurfaceVariant   = TextSecondary,
    outline            = BgBorder,
    secondary          = GreenActive,
    tertiary           = PinkLike,
    error              = Color(0xFFF87171)
)

@Composable
fun NaturalSoundTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = NsTypography,
        content     = content
    )
}
