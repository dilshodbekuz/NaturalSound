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
    val strings = LocalStrings.current
    val c = LocalAppColors.current

    // Aktiv ovozlar sonini ViewModel ga uzatamiz (statistika uchun)
    LaunchedEffect(activeCount) { viewModel.updateActiveCount(activeCount) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bgDeep)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Sarlavha ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
            Text(strings.timerTitle, style = MaterialTheme.typography.headlineMedium, color = c.textPrimary)
            Text(strings.timerSubtitle, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
        }

        // ── Uyqu taymeri ──────────────────────────────────────────────────────
        TimerCard(modifier = Modifier.padding(horizontal = 16.dp)) {

            // Sarlavha
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("😴", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    strings.sleepTimer,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(16.dp))

            // Preset minutlar (faqat to'xtagan holda tanlanadi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PRESET_MINUTES.forEach { min ->
                    PresetChip(
                        label    = if (min >= 60) "${min / 60}${strings.hourAbbr}"
                                   else "${min}${strings.minAbbr}",
                        selected = s.selectedMinutes == min,
                        enabled  = !s.sleepTimerOn,
                        onClick  = { if (!s.sleepTimerOn) viewModel.setTimer(min) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Ishlab turgan vaqtda: vaqt ko'rsatkichi + progress
            AnimatedVisibility(
                visible = s.sleepTimerOn,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))

                    // Qolgan vaqt
                    val totalSec = s.remainingMs / 1000
                    val mm = totalSec / 60; val ss = totalSec % 60
                    Text(
                        text = "%02d:%02d".format(mm, ss),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Text(
                        strings.timeLeft,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // Progress bar
                    val progress = if (s.selectedMinutes > 0)
                        s.remainingMs.toFloat() / (s.selectedMinutes * 60_000f) else 0f
                    LinearProgressIndicator(
                        progress   = { progress },
                        modifier   = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
                        color      = Indigo,
                        trackColor = BgBorder
                    )

                    Spacer(Modifier.height(12.dp))

                    // +5 / +10 daqiqa tugmalari
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = { viewModel.addMinutes(5) },
                            shape   = RoundedCornerShape(10.dp),
                            color   = c.bgDeep,
                            border  = BorderStroke(0.5.dp, c.bgBorder)
                        ) {
                            Text(
                                strings.add5Min,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                color    = c.textSecondary
                            )
                        }
                        Surface(
                            onClick = { viewModel.addMinutes(10) },
                            shape   = RoundedCornerShape(10.dp),
                            color   = c.bgDeep,
                            border  = BorderStroke(0.5.dp, c.bgBorder)
                        ) {
                            Text(
                                strings.add10Min,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                color    = c.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Start / Stop tugmasi
            Button(
                onClick  = { if (s.sleepTimerOn) viewModel.stopTimer() else viewModel.startTimer() },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (s.sleepTimerOn) Color(0xFFE53935) else Indigo
                )
            ) {
                Text(
                    text       = if (s.sleepTimerOn) strings.timerStop else strings.timerStart,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White
                )
            }
        }

        // ── Statistika ────────────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                strings.statistics,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = c.textMuted,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bugungi tinglash vaqti
                val h = s.todaySeconds / 3600
                val m = (s.todaySeconds % 3600) / 60
                val timeStr = when {
                    h > 0 -> "${h}${strings.hourAbbr} ${m}${strings.minAbbr}"
                    m > 0 -> "${m}${strings.minAbbr}"
                    else  -> "—"
                }
                StatCard("🎧", timeStr, strings.statToday, Modifier.weight(1f))
                StatCard("🔥", if (s.streakDays > 0) "${s.streakDays}" else "—", strings.statStreak, Modifier.weight(1f))
                StatCard("🎵", if (s.todaySessions > 0) "${s.todaySessions}" else "—", strings.statSessions, Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ── Yordamchi komponentlar ────────────────────────────────────────────────────

@Composable
private fun TimerCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val c = LocalAppColors.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        color    = c.bgCard,
        border   = BorderStroke(0.5.dp, c.bgBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun PresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val c = LocalAppColors.current
    val bg by animateColorAsState(if (selected) c.indigoBg else c.bgDeep, label = "pc_bg")
    val bd by animateColorAsState(if (selected) Indigo else c.bgBorder, label = "pc_bd")
    Surface(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.height(32.dp),
        shape    = RoundedCornerShape(8.dp),
        color    = bg,
        border   = BorderStroke(if (selected) 1.dp else 0.5.dp, bd)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                fontSize   = 12.sp,
                color      = when {
                    !enabled  -> c.textMuted.copy(alpha = 0.4f)
                    selected  -> IndigoLight
                    else      -> c.textMuted
                },
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    val c = LocalAppColors.current
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        color    = c.bgCard,
        border   = BorderStroke(0.5.dp, c.bgBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(emoji, fontSize = 18.sp)
            Text(value, style = MaterialTheme.typography.titleLarge, color = c.textPrimary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = c.textMuted)
        }
    }
}
