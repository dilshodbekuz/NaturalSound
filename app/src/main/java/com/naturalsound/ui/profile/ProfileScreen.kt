package com.naturalsound.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturalsound.ui.theme.*

private data class LangOpt(val code: String, val flag: String, val label: String)
private val langOpts = listOf(
    LangOpt("uz", "🇺🇿", "O'zbekcha"),
    LangOpt("ru", "🇷🇺", "Русский"),
    LangOpt("en", "🇬🇧", "English"),
)

@Composable
fun ProfileScreen(
    onLanguageChange: (String) -> Unit,
    onThemeChange: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val s = viewModel.uiState.collectAsStateWithLifecycle().value
    val strings = LocalStrings.current
    val c = LocalAppColors.current
    var showLangDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ── Til tanlash dialogi ───────────────────────────────────────────────────
    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            containerColor = c.bgCard,
            tonalElevation = 0.dp,
            shape = RoundedCornerShape(20.dp),
            title = { Text(strings.language, color = c.textPrimary, fontWeight = FontWeight.SemiBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    langOpts.forEach { lang ->
                        val selected = strings.languageName == lang.label
                        Surface(
                            onClick = {
                                onLanguageChange(lang.code)
                                showLangDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) c.indigoBg else c.bgDeep,
                            border = BorderStroke(if (selected) 1.dp else 0.5.dp, if (selected) Indigo else c.bgBorder)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(lang.flag, fontSize = 22.sp)
                                Text(
                                    lang.label,
                                    modifier = Modifier.weight(1f),
                                    color = if (selected) c.textPrimary else c.textSecondary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                                if (selected) Text("✓", color = Indigo, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLangDialog = false }) {
                    Text(strings.cancel, color = c.textMuted)
                }
            }
        )
    }

    // ── Logout tasdiqlash dialogi ─────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor   = c.bgCard,
            tonalElevation   = 0.dp,
            shape            = RoundedCornerShape(20.dp),
            title  = { Text(strings.logout, color = c.textPrimary, fontWeight = FontWeight.SemiBold) },
            text   = { Text(strings.logoutSub, color = c.textMuted, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout(onLogout)
                }) {
                    Text(strings.logout, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(strings.cancel, color = TextMuted)
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().background(c.bgDeep).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Avatar ────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(76.dp).clip(RoundedCornerShape(24.dp)).background(c.indigoBg).border(1.dp, Indigo.copy(0.5f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) { Text("🌿", fontSize = 36.sp) }
            Text(s.userName.ifEmpty { "—" }, style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
            Text(strings.profilePremium, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
        }

        // ── Sozlamalar ────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // ── Global EQ ─────────────────────────────────────────────────────
            SettingsSection(strings.sectionEq) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(4.dp)) {
                    EqSliderRow(label = "Bass", emoji = "🔉", value = s.eqBass, color = Color(0xFFFBBF24), onChange = viewModel::setGlobalBass)
                    EqSliderRow(label = "Treble", emoji = "🔊", value = s.eqTreble, color = Color(0xFF60A5FA), onChange = viewModel::setGlobalTreble)
                    if (s.eqBass != 0f || s.eqTreble != 0f) {
                        Surface(
                            onClick = { viewModel.setGlobalBass(0f); viewModel.setGlobalTreble(0f) },
                            shape = RoundedCornerShape(8.dp), color = c.bgDeep, border = BorderStroke(0.5.dp, c.bgBorder)
                        ) {
                            Text(strings.eqReset, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = c.textMuted)
                        }
                    }
                }
            }

            SettingsSection(strings.sectionSettings) {
                ThemeModeRow(onThemeChange = onThemeChange)
                SettingDivider()
                SettingNavRow(
                    emoji = "🌐",
                    title = strings.language,
                    subtitle = strings.languageName,
                    onClick = { showLangDialog = true }
                )
                SettingDivider()
                SettingNavRow(emoji = "ℹ️", title = strings.aboutApp, subtitle = strings.aboutAppSub)
                SettingDivider()
                SettingNavRow(
                    emoji = "🚪",
                    title = strings.logout,
                    subtitle = strings.logoutSub,
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { showLogoutDialog = true }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Yordamchi komponentlar ────────────────────────────────────────────────────

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val c = LocalAppColors.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = c.textMuted, letterSpacing = 1.5.sp, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = c.bgCard, border = BorderStroke(0.5.dp, c.bgBorder), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun ThemeModeRow(onThemeChange: (String) -> Unit) {
    val c = LocalAppColors.current
    val strings = LocalStrings.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(c.bgDeep),
            contentAlignment = Alignment.Center
        ) { Text(if (c.isDark) "🌙" else "☀️", fontSize = 16.sp) }
        Column(modifier = Modifier.weight(1f)) {
            Text(strings.nightMode, style = MaterialTheme.typography.bodyMedium, color = c.textPrimary)
            Text(strings.nightModeSub, style = MaterialTheme.typography.labelSmall, color = c.textMuted)
        }
        // Light / Dark chip tugmalari
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ThemeChip(label = "☀️", selected = !c.isDark, onClick = { onThemeChange("light") })
            ThemeChip(label = "🌙", selected = c.isDark,  onClick = { onThemeChange("dark") })
        }
    }
}

@Composable
private fun ThemeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val c = LocalAppColors.current
    val bg by animateColorAsState(if (selected) c.indigoBg else c.bgDeep, label = "tc_bg")
    val bd by animateColorAsState(if (selected) Indigo else c.bgBorder,   label = "tc_bd")
    Surface(
        onClick  = onClick,
        modifier = Modifier.size(36.dp),
        shape    = RoundedCornerShape(10.dp),
        color    = bg,
        border   = BorderStroke(if (selected) 1.dp else 0.5.dp, bd)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontSize = 16.sp)
        }
    }
}

@Composable
private fun SettingToggleRow(emoji: String, title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    val c = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val iconBg by animateColorAsState(if (checked) c.indigoBg else c.bgDeep, label = "ibg")
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = c.textPrimary)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = c.textMuted)
        }
        Switch(
            checked = checked, onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Indigo, uncheckedTrackColor = c.bgBorder)
        )
    }
}

@Composable
private fun SettingNavRow(
    emoji: String,
    title: String,
    subtitle: String,
    titleColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(c.bgDeep), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = titleColor ?: c.textPrimary)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = c.textMuted)
        }
        Text("›", fontSize = 20.sp, color = c.textMuted)
    }
}

@Composable
private fun SettingDivider() {
    val c = LocalAppColors.current
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = c.bgBorder, thickness = 0.5.dp)
}

@Composable
private fun EqSliderRow(label: String, emoji: String, value: Float, color: Color, onChange: (Float) -> Unit) {
    val c = LocalAppColors.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(emoji, fontSize = 14.sp, modifier = Modifier.width(20.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = c.textMuted, modifier = Modifier.width(44.dp))
        Slider(
            value = (value + 5f) / 10f,
            onValueChange = { onChange((it * 10f) - 5f) },
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color, inactiveTrackColor = c.bgBorder)
        )
        Text(
            "${if (value > 0) "+" else ""}${value.toInt()}",
            style = MaterialTheme.typography.labelSmall, color = color,
            fontWeight = FontWeight.Medium, modifier = Modifier.width(26.dp), textAlign = TextAlign.End
        )
    }
}
