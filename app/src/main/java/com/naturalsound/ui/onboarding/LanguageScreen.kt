package com.naturalsound.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturalsound.ui.theme.*

private data class LangOption(val code: String, val flag: String, val label: String)

private val langOptions = listOf(
    LangOption("uz", "🇺🇿", "O'zbekcha"),
    LangOption("ru", "🇷🇺", "Русский"),
    LangOption("en", "🇬🇧", "English"),
)

@Composable
fun LanguageScreen(
    onNext: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onThemeChange: (String) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalStrings.current
    val c = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bgDeep)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(c.indigoBg)
                .border(1.5.dp, Indigo.copy(alpha = 0.4f), RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🌿", fontSize = 40.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("NaturalSound", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = c.textPrimary)
        Spacer(Modifier.height(6.dp))
        Text(strings.langSubtitle, fontSize = 14.sp, color = c.textMuted)

        Spacer(Modifier.height(24.dp))

        // ── Tema tanlash ──────────────────────────────────────────────────────
//        Text(
//            strings.nightMode,
//            fontSize = 12.sp,
//            color = c.textMuted,
//            fontWeight = FontWeight.Medium,
//            letterSpacing = 0.5.sp
//        )
//        Spacer(Modifier.height(8.dp))
//        ThemeToggleChips(onThemeChange = onThemeChange)

//        Spacer(Modifier.height(32.dp))

        langOptions.forEach { lang ->
            val selected = s.selectedLang == lang.code
            Surface(
                onClick = {
                    viewModel.selectLanguage(lang.code)
                    onLanguageSelected(lang.code)
                },
                shape = RoundedCornerShape(14.dp),
                color = if (selected) c.indigoBg else c.bgCard,
                border = BorderStroke(
                    width = if (selected) 1.5.dp else 0.5.dp,
                    color = if (selected) Indigo else c.bgBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(lang.flag, fontSize = 28.sp)
                    Text(
                        lang.label,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) c.textPrimary else c.textSecondary,
                        fontSize = 16.sp
                    )
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Indigo),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { viewModel.saveLanguageAndProceed(onNext) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo)
        ) {
            Text(strings.langButton, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ThemeToggleChips(onThemeChange: (String) -> Unit) {
    val c = LocalAppColors.current
    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = c.bgCard,
        border = BorderStroke(0.5.dp, c.bgBorder)
    ) {
        Row(modifier = Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("light" to "☀️  Light", "dark" to "🌙  Dark").forEach { (mode, label) ->
                val selected = (mode == "dark") == c.isDark
                val bg by androidx.compose.animation.animateColorAsState(
                    if (selected) c.indigoBg else Color.Transparent, label = "tb_bg"
                )
                val bd by androidx.compose.animation.animateColorAsState(
                    if (selected) Indigo else Color.Transparent, label = "tb_bd"
                )
                Surface(
                    onClick  = { onThemeChange(mode) },
                    shape    = RoundedCornerShape(10.dp),
                    color    = bg,
                    border   = BorderStroke(if (selected) 1.dp else 0.dp, bd),
                ) {
                    Text(
                        label,
                        modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize  = 13.sp,
                        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                        color     = if (selected) IndigoLight else c.textMuted
                    )
                }
            }
        }
    }
}
