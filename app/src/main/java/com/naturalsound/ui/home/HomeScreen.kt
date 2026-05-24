package com.naturalsound.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturalsound.ads.NativeAdCard
import com.naturalsound.ads.NativeAdManager
import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.model.SoundCategory
import com.naturalsound.ui.components.*
import com.naturalsound.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateMixer: () -> Unit,
    activeSoundIds: Set<String>,
    activeSoundNames: List<String>,
    onToggleSound: (Sound) -> Unit,
    onPauseAll: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = LocalStrings.current
    val c = LocalAppColors.current

    LaunchedEffect(activeSoundIds) { viewModel.updateActiveSounds(activeSoundIds) }

    // ── Native Ad ──────────────────────────────────────────────────────────────
    val context = LocalContext.current
    val adManager = remember { NativeAdManager(context) }
    val nativeAd by adManager.nativeAd.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { adManager.loadAd() }
    DisposableEffect(Unit) { onDispose { adManager.destroyAd() } }

    Scaffold(
        containerColor = c.bgDeep,
        bottomBar = {
            MiniPlayer(
                activeCount  = uiState.activeCount,
                soundNames   = activeSoundNames,
                isPlaying    = true,
                onTogglePlay = onPauseAll
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement   = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                HomeTopBar(activeCount = uiState.activeCount, onMixerClick = onNavigateMixer)
            }
            item(span = { GridItemSpan(2) }) {
                NsSearchBar(
                    query = uiState.searchQuery,
                    onQuery = viewModel::onSearch,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            item(span = { GridItemSpan(2) }) {
                CategoryRow(selected = uiState.selectedCategory, onSelect = viewModel::selectCategory)
            }
            item(span = { GridItemSpan(2) }) {
                SectionHeader(
                    title = when {
                        uiState.searchQuery.isNotBlank() -> strings.homeResults(uiState.sounds.size)
                        uiState.selectedCategory == SoundCategory.ALL -> strings.homePopular
                        else -> strings.categoryLabel(uiState.selectedCategory)
                    }
                )
            }
            if (uiState.isLoading) {
                items(6, span = { GridItemSpan(1) }) { SoundCardSkeleton() }
            }
            // ── Birinchi 6 ta sound ──────────────────────────────────────────
            items(uiState.sounds.take(6), key = { it.id }, span = { GridItemSpan(1) }) { sound ->
                SoundCard(
                    emoji     = sound.emoji,
                    name      = sound.name,
                    subLabel  = strings.categoryLabel(sound.category),
                    isPlaying = sound.id in uiState.activeSoundIds,
                    onTap     = { onToggleSound(sound) },
                )
            }
            // ── Native reklama (6-sounddan keyin, to'liq kenglikda) ──────────
            nativeAd?.let { ad ->
                item(span = { GridItemSpan(2) }) {
                    NativeAdCard(
                        nativeAd = ad,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
            // ── Qolgan soundlar ──────────────────────────────────────────────
            items(uiState.sounds.drop(6), key = { it.id }, span = { GridItemSpan(1) }) { sound ->
                SoundCard(
                    emoji     = sound.emoji,
                    name      = sound.name,
                    subLabel  = strings.categoryLabel(sound.category),
                    isPlaying = sound.id in uiState.activeSoundIds,
                    onTap     = { onToggleSound(sound) },
                )
            }
            uiState.error?.let { err ->
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "⚠️ $err",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }
            }
            if (!uiState.isLoading && uiState.sounds.isEmpty() && uiState.error == null) {
                item(span = { GridItemSpan(2) }) { EmptyState(uiState.searchQuery) }
            }
        }
    }
}

@Composable
private fun HomeTopBar(activeCount: Int, onMixerClick: () -> Unit) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            val c2 = LocalAppColors.current
        Text(strings.homeGreeting, style = MaterialTheme.typography.labelMedium, color = c2.textMuted)
            Text("NaturalSound 🌿", style = MaterialTheme.typography.headlineMedium, color = c2.textPrimary)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (activeCount > 0) ServicePill(activeCount = activeCount)
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(Indigo).clickable { onMixerClick() },
                contentAlignment = Alignment.Center
            ) { Text("XK", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun CategoryRow(selected: SoundCategory, onSelect: (SoundCategory) -> Unit) {
    val strings = LocalStrings.current
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
        items(SoundCategory.entries) { cat ->
            CategoryChip(
                label    = strings.categoryLabel(cat),
                emoji    = cat.emoji,
                selected = cat == selected,
                onClick  = { onSelect(cat) }
            )
        }
    }
}

@Composable
private fun SoundCardSkeleton() {
    val inf = rememberInfiniteTransition(label = "shimmer")
    val alpha by inf.animateFloat(
        initialValue = 0.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "a"
    )
    val c = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = c.bgCard.copy(alpha = alpha),
        border = BorderStroke(0.5.dp, c.bgBorder),
        modifier = Modifier.fillMaxWidth().height(120.dp)
    ) {}
}

@Composable
private fun EmptyState(query: String) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🔇", fontSize = 48.sp)
        val c = LocalAppColors.current
        Text(
            if (query.isNotBlank()) strings.homeNotFound(query) else strings.homeEmpty,
            style = MaterialTheme.typography.titleMedium, color = c.textSecondary
        )
        Text(strings.homeLoading, style = MaterialTheme.typography.bodyMedium, color = c.textMuted)
    }
}
