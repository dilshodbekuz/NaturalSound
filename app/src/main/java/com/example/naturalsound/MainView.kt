package com.example.naturalsound

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.naturalsound.composable.Text16spBold
import com.example.naturalsound.composable.TopBar
import com.example.naturalsound.ui.theme.AppColors
import com.example.naturalsound.ui.theme.NaturalSoundTheme

@Composable
fun MainView(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    NaturalSoundTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.color.background)
                .systemBarsPadding(),
            topBar = { TopBar(onClick = viewModel::resetSound) },
            content = { paddingValues ->
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .background(AppColors.color.background)
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .padding(paddingValues)
                ) {
                    items(viewModel.sounds) { item ->
                        SoundItem(
                            value = item.value,
                            isPlayer = uiState.selectList.contains(item),
                            onClick = {
                                if (uiState.selectList.contains(item)) {
                                    viewModel.stopSound(item)
                                } else viewModel.playSound(item)
                            },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (viewModel.sounds.size % 2 != 0) item { }
                    item { Text16spBold("Animals") }
                    item {}
                    items(viewModel.animalsSound) { item ->
                        SoundItem(
                            value = item.value,
                            isPlayer = false,
                            onClick = { viewModel.playSound(item) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
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