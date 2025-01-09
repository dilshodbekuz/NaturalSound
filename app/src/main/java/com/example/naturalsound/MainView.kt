package com.example.naturalsound

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturalsound.composable.Spacer4
import com.example.naturalsound.composable.TopBar
import com.example.naturalsound.ui.theme.AppColors
import com.example.naturalsound.ui.theme.NaturalSoundTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    NaturalSoundTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.color.background)
                .systemBarsPadding(),
            topBar = { TopBar(onClick = viewModel::resetSound) },
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
                        .background(AppColors.color.background)
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .padding(paddingValues)
                ) {
                    item {
                        AnimatedVisibility(uiState.counter != 0 && uiState.counter != null) {
                            Text(
                                "End Time... ${uiState.counter}s",
                                color = AppColors.color.selectedColor,
                                fontSize = 18.sp
                            )
                        }
                    }
                    item {}
                    items(viewModel.sounds) { item ->
                        SoundItem(
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
                        Column {
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
            "$time s",
            color = AppColors.color.textColor,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SoundItem(value: String, isPlayer: Boolean, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(if (isPlayer) AppColors.color.selectedColor else AppColors.color.darkColor)
        .clickable { onClick() }
        .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
                .background(Color(0xFF5A7380))
                .padding(8.dp),
            painter = painterResource(R.drawable.ic_music_),
            tint = Color.White,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            value,
            fontSize = 16.sp,
            color = AppColors.color.textColor,
            fontWeight = FontWeight.Bold
        )
    }
}