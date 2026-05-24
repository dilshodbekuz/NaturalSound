package com.naturalsound.ui.timer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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

private val PRESET_MINUTES = listOf(15, 30, 45, 60, 90, 120)

@Composable
fun TimerScreen(
    activeCount: Int,
    onFadeOutAndStop: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    var showAlarmPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(BgDeep).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Sarlavha ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
            Text("Taymer", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Text("Avtomatik boshqaruv", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        }

        // ── Uyqu taymeri ──────────────────────────────────────────────────────
        TimerCard(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("😴", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text("Uyqu taymeri", style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                Switch(
                    checked = s.sleepTimerOn,
                    onCheckedChange = { viewModel.toggleSleepTimer() },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Indigo, uncheckedTrackColor = BgBorder)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Katta vaqt ko'rsatkichi
            AnimatedContent(targetState = s.remainingMs, label = "timer") { ms ->
                val totalSec = ms / 1000
                val m = totalSec / 60; val sec = totalSec % 60
                Text(
                    text = "%02d:%02d".format(m, sec),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (s.sleepTimerOn) TextPrimary else TextMuted,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
            Text(
                "qolgan vaqt",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Progress bar
            val progress = if (s.selectedMinutes > 0)
                s.remainingMs.toFloat() / (s.selectedMinutes * 60_000f) else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
                color    = Indigo,
                trackColor = BgBorder
            )

            Spacer(Modifier.height(12.dp))

            // Preset minutlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PRESET_MINUTES.forEach { min ->
                    PresetChip(
                        label    = if (min >= 60) "${min/60}s" else "${min}m",
                        selected = s.selectedMinutes == min,
                        onClick  = { viewModel.setTimer(min) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // +5/+10 qo'shish tugmalari
            if (s.sleepTimerOn) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = { viewModel.addMinutes(5) },
                        shape = RoundedCornerShape(10.dp),
                        color = BgDeep,
                        border = BorderStroke(0.5.dp, BgBorder)
                    ) { Text("+5 daq", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = TextSecondary) }
                    Surface(
                        onClick = { viewModel.addMinutes(10) },
                        shape = RoundedCornerShape(10.dp),
                        color = BgDeep,
                        border = BorderStroke(0.5.dp, BgBorder)
                    ) { Text("+10 daq", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = TextSecondary) }
                }
            }
        }

        // ── Fade Out ──────────────────────────────────────────────────────────
        TimerCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📉", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fade Out", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("To'xtatishdan oldin ovoz sekin pasayadi", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Davomiylik", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("${s.fadeOutSeconds} soniya", style = MaterialTheme.typography.labelSmall, color = IndigoLight, fontWeight = FontWeight.Medium)
            }
            Slider(
                value = s.fadeOutSeconds.toFloat(),
                onValueChange = { viewModel.setFadeOut(it.toInt()) },
                valueRange = 10f..120f,
                steps = 10,
                colors = SliderDefaults.colors(thumbColor = Indigo, activeTrackColor = Indigo, inactiveTrackColor = BgBorder)
            )
            if (s.sleepTimerOn && activeCount > 0) {
                Spacer(Modifier.height(4.dp))
                Surface(
                    onClick = onFadeOutAndStop,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1A0808),
                    border = BorderStroke(0.5.dp, Color(0xFF7F1D1D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Hozir fade out bilan to'xtat",
                        modifier = Modifier.padding(10.dp),
                        color = Color(0xFFF87171),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── Alarm ─────────────────────────────────────────────────────────────
        TimerCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏰", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Uyg'onish alarmi", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text("Tabiat ovozi bilan uyg'onish", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                Switch(
                    checked = s.alarmEnabled,
                    onCheckedChange = { viewModel.toggleAlarm() },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Indigo, uncheckedTrackColor = BgBorder)
                )
            }
            Spacer(Modifier.height(12.dp))
            Surface(
                onClick = { showAlarmPicker = true },
                shape = RoundedCornerShape(14.dp),
                color = BgDeep,
                border = BorderStroke(0.5.dp, if (s.alarmEnabled) Indigo.copy(0.5f) else BgBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "%02d:%02d".format(s.alarmHour, s.alarmMinute),
                    modifier = Modifier.padding(vertical = 14.dp),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (s.alarmEnabled) TextPrimary else TextMuted,
                    textAlign = TextAlign.Center,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        // ── Statistika ────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("STATISTIKA", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextMuted, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("🎧", "${s.todayMinutes / 60}s ${s.todayMinutes % 60}d", "Bugun", Modifier.weight(1f))
                StatCard("🔥", "${s.streakDays}", "Kun seriya", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("💾", "${s.offlineCount}", "Offline", Modifier.weight(1f))
                StatCard("❤️", "${s.favoriteCount}", "Sevimli", Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // ── Alarm vaqt tanlash dialogi ─────────────────────────────────────────────
    if (showAlarmPicker) {
        AlarmPickerDialog(
            hour = s.alarmHour, minute = s.alarmMinute,
            onConfirm = { h, m -> viewModel.setAlarmTime(h, m); showAlarmPicker = false },
            onDismiss = { showAlarmPicker = false }
        )
    }
}

@Composable
private fun TimerCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = BgCard, border = BorderStroke(0.5.dp, BgBorder)) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun PresetChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg by animateColorAsState(if (selected) IndigoBg else BgDeep, label = "pc_bg")
    val bd by animateColorAsState(if (selected) Indigo else BgBorder, label = "pc_bd")
    Surface(
        onClick = onClick, modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(8.dp), color = bg,
        border = BorderStroke(if (selected) 1.dp else 0.5.dp, bd)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontSize = 12.sp, color = if (selected) IndigoLight else TextMuted, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
        }
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = BgCard, border = BorderStroke(0.5.dp, BgBorder)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(emoji, fontSize = 18.sp)
            Text(value, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
private fun AlarmPickerDialog(hour: Int, minute: Int, onConfirm: (Int, Int) -> Unit, onDismiss: () -> Unit) {
    var h by remember { mutableIntStateOf(hour) }
    var m by remember { mutableIntStateOf(minute) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCard, tonalElevation = 0.dp, shape = RoundedCornerShape(20.dp),
        title = { Text("Alarm vaqti", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
        text = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                NumberPicker(value = h, range = 0..23, onValue = { h = it })
                Text(":", fontSize = 32.sp, color = TextPrimary, modifier = Modifier.padding(horizontal = 8.dp))
                NumberPicker(value = m, range = 0..59, onValue = { m = it })
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(h, m) }) { Text("Saqlash", color = Indigo, fontWeight = FontWeight.SemiBold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Bekor", color = TextMuted) } }
    )
}

@Composable
private fun NumberPicker(value: Int, range: IntRange, onValue: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Surface(onClick = { onValue(if (value < range.last) value + 1 else range.first) }, shape = CircleShape, color = BgDeep, border = BorderStroke(0.5.dp, BgBorder), modifier = Modifier.size(36.dp)) {
            Box(contentAlignment = Alignment.Center) { Text("▲", fontSize = 14.sp, color = TextSecondary) }
        }
        Text("%02d".format(value), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        Surface(onClick = { onValue(if (value > range.first) value - 1 else range.last) }, shape = CircleShape, color = BgDeep, border = BorderStroke(0.5.dp, BgBorder), modifier = Modifier.size(36.dp)) {
            Box(contentAlignment = Alignment.Center) { Text("▼", fontSize = 14.sp, color = TextSecondary) }
        }
    }
}
