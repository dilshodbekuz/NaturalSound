package com.example.naturalsound.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.naturalsound.R
import com.example.naturalsound.composable.MainTopBar
import com.example.naturalsound.composable.Spacer4
import com.example.naturalsound.composable.Text24spBold
import com.example.naturalsound.splash.SnowfallEffect
import com.example.naturalsound.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.selectList.isEmpty()) {
        if (uiState.counter != 0) {
            viewModel.timerJob?.cancel()
            viewModel.setTimer(0)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        SnowfallEffect()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                Column {
                    MainTopBar(counter = uiState.counter, onClick = viewModel::resetSound)
                }
            },
            floatingActionButton = {
                AnimatedVisibility(uiState.selectList.isNotEmpty()) {
                    FloatingActionButton(
                        shape = RoundedCornerShape(50),
                        containerColor = AppColors.color.selectedColor,
                        onClick = {
                            showBottomSheet = true
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.timer),
                            contentDescription = null
                        )
                    }
                }
            },
            content = { paddingValues ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .padding(paddingValues)
                ) {
                    items(viewModel.sounds) { item ->
                        SoundItem(
                            image = item.image,
                            value = item.value,
                            isPlayer = uiState.selectList.contains(item),
                            onClick = {
                                if (uiState.selectList.contains(item)) {
                                    viewModel.stopSound(item)
                                } else viewModel.playSound(context, item)
                            },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (showBottomSheet) {
                    ModalBottomSheet(
                        containerColor = AppColors.color.background,
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = sheetState
                    ) {
                        Column(modifier = Modifier.navigationBarsPadding()) {
                            Text(
                                "Set Timer",
                                fontSize = 18.sp,
                                color = AppColors.color.selectedColor,
                                fontWeight = FontWeight.W700,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TimerItem(
                                    modifier = Modifier.weight(1f),
                                    time = 5,
                                    onClick = {
                                        showBottomSheet = false
                                        viewModel.setTimer(5)
                                    }
                                )
                                TimerItem(
                                    modifier = Modifier.weight(1f),
                                    time = 15,
                                    onClick = {
                                        showBottomSheet = false
                                        viewModel.setTimer(15)
                                    }
                                )
                                TimerItem(
                                    modifier = Modifier.weight(1f),
                                    time = 30,
                                    onClick = {
                                        showBottomSheet = false
                                        viewModel.setTimer(30)
                                    }
                                )
                                TimerItem(
                                    modifier = Modifier.weight(1f),
                                    time = 60,
                                    onClick = {
                                        showBottomSheet = false
                                        viewModel.setTimer(60)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun TimerItem(modifier: Modifier, time: Int, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.color.darkColor)
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.timer),
            contentDescription = null,
            tint = Color.White
        )
        Spacer4()
        Text(
            "$time min",
            color = AppColors.color.textColor,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SoundItem(image: Int?, value: String, isPlayer: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(18.dp))
            .size(100.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = image,
            contentDescription = null,
            placeholder = painterResource(R.drawable.download),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.7f)
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(18.dp),
                    color = if (isPlayer) AppColors.color.selectedColor else Color.Transparent
                ),
            contentScale = ContentScale.Crop
        )
        Text24spBold(
            modifier = Modifier.align(Alignment.Center),
            text = value,
            textAlign = TextAlign.Center,
            color = if (isPlayer) AppColors.color.selectedColor else AppColors.color.textColor
        )
    }
}