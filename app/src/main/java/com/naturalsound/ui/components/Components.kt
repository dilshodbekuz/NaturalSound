package com.naturalsound.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naturalsound.ui.theme.*

// ── Servis pill ──────────────────────────────────────────────────────────────
@Composable
fun ServicePill(activeCount: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Reverse),
        label = "dot"
    )
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1A2F1A),
        border = BorderStroke(0.5.dp, Color(0xFF2D5A2D))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(GreenActive.copy(alpha = alpha)))
            Text("$activeCount ta ovoz", color = GreenActive, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Kategoriya chip ──────────────────────────────────────────────────────────
@Composable
fun CategoryChip(label: String, emoji: String, selected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(if (selected) Indigo else BgCard, label = "chip_bg")
    val tc by animateColorAsState(if (selected) Color.White else TextMuted, label = "chip_tc")
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = if (!selected) BorderStroke(0.5.dp, BgBorder) else null,
        modifier = Modifier.height(34.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (selected) Text(emoji, fontSize = 13.sp)
            Text(label, color = tc, style = MaterialTheme.typography.labelMedium)
        }
    }
}

// ── Animatsiyali to'lqin ─────────────────────────────────────────────────────
@Composable
fun PlayingWaveIcon(modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "wave")
    val heights = (0..4).map { i ->
        inf.animateFloat(
            initialValue = 0.3f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(300 + i * 70, easing = EaseInOut), RepeatMode.Reverse
            ),
            label = "h$i"
        )
    }
    Box(
        modifier = modifier.clip(CircleShape).background(Indigo),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)
        ) {
            heights.forEach { h ->
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height((14 * h.value).dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(Color.White)
                )
            }
        }
    }
}

// ── Sound karta ──────────────────────────────────────────────────────────────
@Composable
fun SoundCard(
    emoji: String,
    name: String,
    subLabel: String,
    isPlaying: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(if (isPlaying) Indigo else BgBorder, label = "bc")
    val bgColor     by animateColorAsState(if (isPlaying) IndigoBg else BgCard, label = "bg")
    val borderWidth by animateDpAsState(if (isPlaying) 1.dp else 0.5.dp, label = "bw")

    Surface(
        onClick  = onTap,
        modifier = modifier,
        shape    = RoundedCornerShape(18.dp),
        color    = bgColor,
        border   = BorderStroke(borderWidth, borderColor)
    ) {
        Box(Modifier.padding(14.dp).fillMaxWidth()) {
            Column {
                Text(emoji, fontSize = 28.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text(name, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1)
                Spacer(Modifier.height(3.dp))
                Text(subLabel, style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            if (isPlaying) {
                PlayingWaveIcon(modifier = Modifier.align(Alignment.TopEnd).size(26.dp))
            }
        }
    }
}

// ── Mini Player ───────────────────────────────────────────────────────────────
@Composable
fun MiniPlayer(
    activeCount: Int,
    soundNames: List<String>,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeCount == 0) return
    Surface(modifier = modifier.fillMaxWidth(), color = BgCard, border = BorderStroke(0.5.dp, BgBorder)) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🎛️", fontSize = 22.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text("$activeCount ta ovoz aralashmasi", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(soundNames.take(3).joinToString(" · "), style = MaterialTheme.typography.labelSmall, color = TextMuted, maxLines = 1)
            }
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Indigo).clickable { onTogglePlay() },
                contentAlignment = Alignment.Center
            ) { Text(if (isPlaying) "⏸" else "▶", fontSize = 15.sp) }
        }
    }
}

// ── Search bar ────────────────────────────────────────────────────────────────
@Composable
fun NsSearchBar(query: String, onQuery: (String) -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = BgCard, border = BorderStroke(0.5.dp, BgBorder)) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🔍", fontSize = 15.sp)
            BasicTextField(
                value = query, onValueChange = onQuery, singleLine = true, modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                decorationBox = { inner ->
                    if (query.isEmpty()) Text("Ovoz qidirish...", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    inner()
                }
            )
            if (query.isNotEmpty()) Text("✕", fontSize = 14.sp, color = TextMuted, modifier = Modifier.clickable { onQuery("") })
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        if (actionLabel != null && onAction != null)
            Text(actionLabel, style = MaterialTheme.typography.labelMedium, color = Indigo, modifier = Modifier.clickable { onAction() })
    }
}
