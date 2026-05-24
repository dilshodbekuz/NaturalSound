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

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    var showEqSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(BgDeep).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Avatar va profil ──────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(76.dp).clip(RoundedCornerShape(24.dp)).background(IndigoBg).border(1.dp, Indigo.copy(0.5f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) { Text("🌿", fontSize = 36.sp) }
            Text("Xakimov", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text("Premium foydalanuvchi", style = MaterialTheme.typography.bodyMedium, color = TextMuted)

            // Statistika qatori
            Row(
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ProfileStat("%.1f".format(s.totalListenHours) + "s", "Tinglash")
                Divider(modifier = Modifier.height(32.dp).width(0.5.dp), color = BgBorder)
                ProfileStat("${s.streakDays}", "Kunlik seriya 🔥")
                Divider(modifier = Modifier.height(32.dp).width(0.5.dp), color = BgBorder)
                ProfileStat("${s.downloadedSounds.size}", "Offline")
            }
        }

        // ── Sozlamalar ────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettingsSection("SOZLAMALAR") {
                SettingToggleRow(
                    emoji = "📶", title = "Wi-Fi da avtomatik yuklash",
                    subtitle = "Yangi ovozlarni offline saqlash",
                    checked = s.autoDownloadOnWifi, onToggle = viewModel::toggleAutoDownload
                )
                SettingDivider()
                SettingToggleRow(
                    emoji = "🔔", title = "Xabarnomalar",
                    subtitle = "Service va yangi ovozlar haqida",
                    checked = s.notificationsOn, onToggle = viewModel::toggleNotifications
                )
                SettingDivider()
                SettingToggleRow(
                    emoji = "🌙", title = "Tungi rejim",
                    subtitle = "Ekran yorqinligini kamaytirish",
                    checked = s.nightMode, onToggle = viewModel::toggleNightMode
                )
            }

            // ── Global EQ ─────────────────────────────────────────────────────
            SettingsSection("GLOBAL EQ") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(4.dp)) {
                    EqSliderRow(
                        label = "Bass", emoji = "🔉", value = s.eqBass,
                        color = Color(0xFFFBBF24), onChange = viewModel::setGlobalBass
                    )
                    EqSliderRow(
                        label = "Treble", emoji = "🔊", value = s.eqTreble,
                        color = Color(0xFF60A5FA), onChange = viewModel::setGlobalTreble
                    )
                    if (s.eqBass != 0f || s.eqTreble != 0f) {
                        Surface(
                            onClick = { viewModel.setGlobalBass(0f); viewModel.setGlobalTreble(0f) },
                            shape = RoundedCornerShape(8.dp), color = BgDeep, border = BorderStroke(0.5.dp, BgBorder)
                        ) {
                            Text("Standartga qaytarish", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }

            // ── Offline fayllar ───────────────────────────────────────────────
            if (s.downloadedSounds.isNotEmpty()) {
                SettingsSection("OFFLINE FAYLLAR (${s.downloadedSounds.size})") {
                    s.downloadedSounds.forEach { sound ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(sound.emoji, fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sound.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                Text("Offline · MP3", style = MaterialTheme.typography.labelSmall, color = GreenActive)
                            }
                            Text("🗑", fontSize = 16.sp, color = TextMuted)
                        }
                        if (sound != s.downloadedSounds.last()) SettingDivider()
                    }
                }
            }

            // ── Boshqa havolalar ──────────────────────────────────────────────
            SettingsSection("BOSHQA") {
                SettingNavRow(emoji = "☁️", title = "Firebase sinxronizatsiya", subtitle = "Yangi ovozlar avtomatik yangilanadi")
                SettingDivider()
                SettingNavRow(emoji = "ℹ️", title = "Ilova haqida", subtitle = "NaturalSound v1.0")
                SettingDivider()
                SettingNavRow(emoji = "🚪", title = "Chiqish", subtitle = "Hisobdan chiqish", titleColor = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Yordamchi komponentlar ────────────────────────────────────────────────────

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextMuted, letterSpacing = 1.5.sp, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = BgCard, border = BorderStroke(0.5.dp, BgBorder), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun SettingToggleRow(emoji: String, title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val iconBg by animateColorAsState(if (checked) IndigoBg else BgDeep, label = "ibg")
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
        Switch(
            checked = checked, onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Indigo, uncheckedTrackColor = BgBorder)
        )
    }
}

@Composable
private fun SettingNavRow(emoji: String, title: String, subtitle: String, titleColor: Color = TextPrimary) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(BgDeep), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = titleColor)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
        Text("›", fontSize = 20.sp, color = TextMuted)
    }
}

@Composable
private fun SettingDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BgBorder, thickness = 0.5.dp)
}

@Composable
private fun EqSliderRow(label: String, emoji: String, value: Float, color: Color, onChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(emoji, fontSize = 14.sp, modifier = Modifier.width(20.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(44.dp))
        Slider(
            value = (value + 5f) / 10f,
            onValueChange = { onChange((it * 10f) - 5f) },
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color, inactiveTrackColor = BgBorder)
        )
        Text(
            "${if (value > 0) "+" else ""}${value.toInt()}",
            style = MaterialTheme.typography.labelSmall, color = color,
            fontWeight = FontWeight.Medium, modifier = Modifier.width(26.dp), textAlign = TextAlign.End
        )
    }
}
