package com.naturalsound.ui.mixer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturalsound.ads.NativeAdCard
import com.naturalsound.ads.NativeAdManager
import com.naturalsound.domain.model.Sound
import com.naturalsound.ui.theme.*

@Composable
fun MixerScreen(
    activeSoundIds: Set<String>,
    currentVolumes: Map<String, Float> = emptyMap(),
    onStopSound: (String) -> Unit,
    onSetVolume: (String, Float) -> Unit,
    onPauseAll: () -> Unit,
    onResumeAll: () -> Unit,
    onStopAll: () -> Unit,
    onAddSound: () -> Unit,
    viewModel: MixerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val c = LocalAppColors.current
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(activeSoundIds, currentVolumes) {
        viewModel.syncActiveSounds(activeSoundIds, currentVolumes)
    }

    // ── Native Ad ──────────────────────────────────────────────────────────────
    val context = LocalContext.current
    val adManager = remember { NativeAdManager(context) }
    val nativeAd by adManager.nativeAd.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { adManager.loadAd() }
    DisposableEffect(Unit) { onDispose { adManager.destroyAd() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bgDeep)
    ) {
        // ── Top bar ──────────────────────────────────────────────────────────
        MixerTopBar(
            activeCount  = uiState.activeSounds.size,
            isPlaying    = uiState.isAllPlaying,
            onTogglePlay = {
                if (uiState.isAllPlaying) onPauseAll() else onResumeAll()
                viewModel.toggleAllPlaying()
            },
            onStopAll = onStopAll
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Sahnalar ─────────────────────────────────────────────────────
            item {
                SceneSection(
                    scenes = uiState.scenes,
                    selectedScene = uiState.selectedScene,
                    onSelect = viewModel::selectScene
                )
            }

            // ── Aktiv ovozlar label ──────────────────────────────────────────
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "AKTIV OVOZLAR",
                        fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        color = c.textMuted, letterSpacing = 1.5.sp
                    )
                    if (uiState.activeSounds.isNotEmpty()) {
                        Surface(shape = CircleShape, color = Indigo) {
                            Text(
                                "${uiState.activeSounds.size}",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp),
                                fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── Har bir sound mixer item ──────────────────────────────────────
            if (uiState.activeSounds.isEmpty()) {
                item { EmptyMixer(onAddSound = onAddSound) }
            } else {
                items(uiState.activeSounds, key = { it.id }) { sound ->
                    val volume = uiState.volumes[sound.id] ?: 1f
                    val eq = uiState.eqSettings[sound.id] ?: EqSettings()
                    MixerSoundItem(
                        sound = sound,
                        volume = volume,
                        eq = eq,
                        onVolume = { v ->
                            viewModel.setVolume(sound.id, v)
                            onSetVolume(sound.id, v)
                        },
                        onBass = { b -> viewModel.setEq(sound.id, bass = b) },
                        onTreble = { t -> viewModel.setEq(sound.id, treble = t) },
                        onRemove = { onStopSound(sound.id) },
//                        modifier = Modifier.animateItem()
                    )
                }
            }

            // ── Ovoz qo'shish tugmasi ─────────────────────────────────────────
            item {
                AddSoundButton(onClick = onAddSound)
            }

            // ── Native reklama ────────────────────────────────────────────────
            nativeAd?.let { ad ->
                item {
                    NativeAdCard(
                        nativeAd = ad,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── Umumiy boshqaruv ──────────────────────────────────────────────
            if (uiState.activeSounds.isNotEmpty()) {
                item {
                    MasterControls(
                        isPlaying = uiState.isAllPlaying,
                        onPause = { onPauseAll(); viewModel.setAllPlaying(false) },
                        onResume = { onResumeAll(); viewModel.setAllPlaying(true) },
                        onStop = onStopAll
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    // ── Scene saqlash dialogi ─────────────────────────────────────────────────
    if (showSaveDialog) {
        SaveSceneDialog(
            onConfirm = { name, emoji ->
                viewModel.saveCurrentAsScene(name, emoji)
                showSaveDialog = false
            },
            onDismiss = { showSaveDialog = false }
        )
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────
@Composable
private fun MixerTopBar(
    activeCount: Int,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onStopAll: () -> Unit
) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Mixer", style = MaterialTheme.typography.headlineMedium, color = c.textPrimary)
            Text(
                if (activeCount == 0) "Hech narsa ijroetilmayapti"
                else "$activeCount ta aktiv ovoz",
                style = MaterialTheme.typography.bodyMedium, color = c.textMuted
            )
        }
        if (activeCount > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconChip(emoji = if (isPlaying) "⏸" else "▶", onClick = onTogglePlay, tint = Indigo)
                IconChip(emoji = "⏹", onClick = onStopAll, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun IconChip(emoji: String, onClick: () -> Unit, tint: Color? = null) {
    val c = LocalAppColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = c.bgCard,
        border = BorderStroke(0.5.dp, (tint ?: c.bgBorder).copy(alpha = 0.6f)),
        modifier = Modifier.size(38.dp)
    ) {
        Box(contentAlignment = Alignment.Center) { Text(emoji, fontSize = 17.sp) }
    }
}

// ── Sahnalar section ──────────────────────────────────────────────────────────
@Composable
private fun SceneSection(
    scenes: List<SoundScene>,
    selectedScene: String?,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val c = LocalAppColors.current
        Text(
            "SAHNALAR",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = c.textMuted,
            letterSpacing = 1.5.sp
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(scenes, key = { it.id }) { scene ->
                val selected = scene.id == selectedScene
                val bg by animateColorAsState(if (selected) c.indigoBg else c.bgCard, label = "sc_bg")
                val border by animateColorAsState(
                    if (selected) Indigo else c.bgBorder,
                    label = "sc_bd"
                )
                Surface(
                    onClick = { onSelect(scene.id) },
                    shape = RoundedCornerShape(14.dp),
                    color = bg,
                    border = BorderStroke(if (selected) 1.dp else 0.5.dp, border),
                    modifier = Modifier
                        .width(88.dp)
                        .height(72.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(scene.emoji, fontSize = 22.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            scene.name,
                            fontSize = 11.sp,
                            color = if (selected) IndigoLight else c.textMuted,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// ── Har bir sound uchun mixer item ────────────────────────────────────────────
@Composable
private fun MixerSoundItem(
    sound: Sound,
    volume: Float,
    eq: EqSettings,
    onVolume: (Float) -> Unit,
    onBass: (Float) -> Unit,
    onTreble: (Float) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = LocalAppColors.current
    var showEq by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = c.bgCard,
        border = BorderStroke(0.5.dp, c.bgBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Sarlavha qatori ───────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.bgDeep),
                    contentAlignment = Alignment.Center
                ) { Text(sound.emoji, fontSize = 22.sp) }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        sound.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = c.textPrimary
                    )
                    Text(
                        sound.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = c.textMuted
                    )
                }

                // EQ tugmasi
                Surface(
                    onClick = { showEq = !showEq },
                    shape = RoundedCornerShape(8.dp),
                    color = if (showEq) c.indigoBg else c.bgDeep,
                    border = BorderStroke(0.5.dp, if (showEq) Indigo else c.bgBorder),
                    modifier = Modifier.size(32.dp)
                ) { Box(contentAlignment = Alignment.Center) { Text("🎚", fontSize = 14.sp) } }

                // O'chirish
                Surface(
                    onClick = onRemove,
                    shape = RoundedCornerShape(8.dp),
                    color = c.bgDeep,
                    border = BorderStroke(0.5.dp, c.bgBorder),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "✕",
                            fontSize = 13.sp,
                            color = c.textMuted
                        )
                    }
                }
            }

            // ── Volume slider ─────────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Ovoz balandligi",
                        style = MaterialTheme.typography.labelSmall,
                        color = c.textMuted
                    )
                    Text(
                        "${(volume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoLight,
                        fontWeight = FontWeight.Medium
                    )
                }
                MixSlider(value = volume, onValueChange = onVolume, activeColor = Indigo)
            }

            // ── Teglar ────────────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SoundTag(text = "Online", color = TextMuted)
                SoundTag(text = "Loop", color = TextMuted)
                if (showEq && (eq.bass != 0f || eq.treble != 0f)) {
                    if (eq.bass != 0f) SoundTag(
                        text = "Bass ${if (eq.bass > 0) "+" else ""}${eq.bass.toInt()}",
                        color = Color(0xFFFBBF24)
                    )
                    if (eq.treble != 0f) SoundTag(
                        text = "Treble ${if (eq.treble > 0) "+" else ""}${eq.treble.toInt()}",
                        color = Color(0xFF60A5FA)
                    )
                }
            }

            // ── EQ panel (kengaytiriladigan) ──────────────────────────────────
            AnimatedVisibility(
                visible = showEq,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                EqPanel(eq = eq, onBass = onBass, onTreble = onTreble)
            }
        }
    }
}

// ── EQ panel ──────────────────────────────────────────────────────────────────
@Composable
private fun EqPanel(eq: EqSettings, onBass: (Float) -> Unit, onTreble: (Float) -> Unit) {
    val c = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = c.bgDeep,
        border = BorderStroke(0.5.dp, c.bgBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "EQ sozlamasi",
                style = MaterialTheme.typography.labelMedium,
                color = c.textSecondary
            )
            EqRow(
                label = "Bass",
                emoji = "🔉",
                value = eq.bass,
                color = Color(0xFFFBBF24),
                onChange = onBass
            )
            EqRow(
                label = "Treble",
                emoji = "🔊",
                value = eq.treble,
                color = Color(0xFF60A5FA),
                onChange = onTreble
            )
        }
    }
}

@Composable
private fun EqRow(
    label: String,
    emoji: String,
    value: Float,
    color: Color,
    onChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(emoji, fontSize = 14.sp, modifier = Modifier.width(20.dp))
        val c = LocalAppColors.current
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = c.textMuted,
            modifier = Modifier.width(44.dp)
        )
        MixSlider(
            value = (value + 5f) / 10f,   // -5..+5  →  0..1
            onValueChange = { onChange((it * 10f) - 5f) },
            activeColor = color,
            modifier = Modifier.weight(1f)
        )
        Text(
            "${if (value > 0) "+" else ""}${value.toInt()}",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(26.dp),
            textAlign = TextAlign.End
        )
    }
}

// ── Umumiy slider komponenti ──────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MixSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color = Indigo,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = activeColor,
            activeTrackColor = activeColor,
            inactiveTrackColor = LocalAppColors.current.bgBorder
        ),
        thumb = {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(activeColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                )
            }
        }
    )
}

// ── Tag komponenti ────────────────────────────────────────────────────────────
@Composable
private fun SoundTag(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.25f))
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            fontSize = 11.sp,
            color = color
        )
    }
}

// ── Bo'sh holat ───────────────────────────────────────────────────────────────
@Composable
private fun EmptyMixer(onAddSound: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("🎛️", fontSize = 52.sp)
        val c = LocalAppColors.current
        Text(
            "Hech narsa qo'shilmagan",
            style = MaterialTheme.typography.titleMedium,
            color = c.textSecondary
        )
        Text(
            "Home ekranda ovozni bosing",
            style = MaterialTheme.typography.bodyMedium,
            color = c.textMuted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Surface(
            onClick = onAddSound,
            shape = RoundedCornerShape(14.dp),
            color = c.indigoBg,
            border = BorderStroke(1.dp, Indigo)
        ) {
            Text(
                "＋ Ovoz tanlash",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                color = IndigoLight,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Ovoz qo'shish tugmasi ─────────────────────────────────────────────────────
@Composable
private fun AddSoundButton(onClick: () -> Unit) {
    val c = LocalAppColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = c.bgCard,
        border = BorderStroke(1.dp, c.bgBorder.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("＋", fontSize = 18.sp, color = Indigo)
            Spacer(Modifier.width(8.dp))
            Text("Ovoz qo'shish", style = MaterialTheme.typography.titleMedium, color = Indigo)
        }
    }
}

// ── Master boshqaruv ──────────────────────────────────────────────────────────
@Composable
private fun MasterControls(
    isPlaying: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    val c = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = c.bgCard,
        border = BorderStroke(0.5.dp, c.bgBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Boshqaruv",
                style = MaterialTheme.typography.titleMedium,
                color = c.textSecondary,
                modifier = Modifier.weight(1f)
            )
            Surface(
                onClick = if (isPlaying) onPause else onResume,
                shape = RoundedCornerShape(12.dp),
                color = c.indigoBg,
                border = BorderStroke(0.5.dp, Indigo),
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        if (isPlaying) "⏸  Pauza" else "▶  Davom",
                        color = IndigoLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Surface(
                onClick = onStop,
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A0808),
                border = BorderStroke(0.5.dp, Color(0xFF7F1D1D)),
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "⏹  To'xtat",
                        color = Color(0xFFF87171),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ── Scene saqlash dialogi ─────────────────────────────────────────────────────
@Composable
private fun SaveSceneDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🎵") }
    val emojis = listOf("🎵", "🌙", "🎯", "🧘", "📖", "☀️", "🌧️", "🏔️", "🌊")

    val c = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.bgCard,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Sahnani saqlash", color = c.textPrimary, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Emoji tanlash
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(emojis) { e ->
                        Surface(
                            onClick = { emoji = e },
                            shape = CircleShape,
                            color = if (e == emoji) c.indigoBg else c.bgDeep,
                            border = BorderStroke(
                                if (e == emoji) 1.dp else 0.5.dp,
                                if (e == emoji) Indigo else c.bgBorder
                            ),
                            modifier = Modifier.size(38.dp)
                        ) { Box(contentAlignment = Alignment.Center) { Text(e, fontSize = 18.sp) } }
                    }
                }
                // Nom
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Sahna nomi...", color = c.textMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo,
                        unfocusedBorderColor = c.bgBorder,
                        focusedTextColor = c.textPrimary,
                        unfocusedTextColor = c.textPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, emoji) }) {
                Text("Saqlash", color = Indigo, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Bekor", color = c.textMuted) }
        }
    )
}
