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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    // Service ID'larini VM ga uzatish
    LaunchedEffect(activeSoundIds) {
        viewModel.updateActiveSounds(activeSoundIds)
    }

    Scaffold(
        containerColor = BgDeep,
        bottomBar = {
            MiniPlayer(
                activeCount = uiState.activeCount,
                soundNames  = activeSoundNames,
                isPlaying   = true,
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
            // Top bar
            item(span = { GridItemSpan(2) }) {
                HomeTopBar(activeCount = uiState.activeCount, onMixerClick = onNavigateMixer)
            }
            // Search
            item(span = { GridItemSpan(2) }) {
                NsSearchBar(query = uiState.searchQuery, onQuery = viewModel::onSearch, modifier = Modifier.padding(top = 4.dp))
            }
            // Kategoriyalar
            item(span = { GridItemSpan(2) }) {
                CategoryRow(selected = uiState.selectedCategory, onSelect = viewModel::selectCategory)
            }
            // Sarlavha
            item(span = { GridItemSpan(2) }) {
                SectionHeader(
                    title = when {
                        uiState.searchQuery.isNotBlank() -> "Natijalar (${uiState.sounds.size})"
                        uiState.selectedCategory == SoundCategory.ALL -> "Mashhur ovozlar"
                        else -> uiState.selectedCategory.label
                    }
                )
            }
            // Skeleton
            if (uiState.isLoading) {
                items(6, span = { GridItemSpan(1) }) { SoundCardSkeleton() }
            }
            // Soundlar
            items(uiState.sounds, key = { it.id }, span = { GridItemSpan(1) }) { sound ->
                SoundCard(
                    emoji     = sound.emoji,
                    name      = sound.name,
                    subLabel  = sound.category.label,
                    isPlaying = sound.id in uiState.activeSoundIds,
                    onTap     = { onToggleSound(sound) },
                )
            }
            // Error state
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
            // Empty state
            if (!uiState.isLoading && uiState.sounds.isEmpty() && uiState.error == null) {
                item(span = { GridItemSpan(2) }) { EmptyState(uiState.searchQuery) }
            }
        }
    }
}

@Composable
private fun HomeTopBar(activeCount: Int, onMixerClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Xush kelibsiz,", style = MaterialTheme.typography.labelMedium, color = TextMuted)
            Text("NaturalSound 🌿", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
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
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
        items(SoundCategory.entries) { cat ->
            CategoryChip(label = cat.label, emoji = cat.emoji, selected = cat == selected, onClick = { onSelect(cat) })
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
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = BgCard.copy(alpha = alpha),
        border = BorderStroke(0.5.dp, BgBorder),
        modifier = Modifier.fillMaxWidth().height(120.dp)
    ) {}
}

@Composable
private fun EmptyState(query: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("🔇", fontSize = 48.sp)
        Text(if (query.isNotBlank()) "«$query» topilmadi" else "Ovozlar yo'q", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
        Text("Firebase'dan yuklanmoqda...", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
    }
}

