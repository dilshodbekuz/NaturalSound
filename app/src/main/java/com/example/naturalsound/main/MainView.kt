package com.example.naturalsound.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults.LinearStrokeCap
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.naturalsound.R
import com.example.naturalsound.composable.MainTopBar
import com.example.naturalsound.composable.Spacer12
import com.example.naturalsound.composable.Spacer16
import com.example.naturalsound.composable.Spacer4
import com.example.naturalsound.composable.Text24spBold
import com.example.naturalsound.composable.snowfall
import com.example.naturalsound.ui.theme.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    viewModel: MainViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    BackHandler {
        if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            scope.launch {
                bottomSheetScaffoldState.bottomSheetState.hide()
            }
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.selectList.isEmpty()) {
        bottomSheetScaffoldState.bottomSheetState.hide()
        if (uiState.counter != 0) {
            viewModel.timerJob?.cancel()
            viewModel.setTimer(0)
            viewModel.setSheetContent(BottomSheetContentType.Times)
        }
    }
    val brush =
        Brush.verticalGradient(
            listOf(
                Color(0xFF000000),
                Color(0xFF006059)
            )
        )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .snowfall()
            .background(brush = brush)
    ) {
        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            containerColor = Color.Transparent,
            sheetPeekHeight = 0.dp,
            scaffoldState = bottomSheetScaffoldState,
            sheetContainerColor = AppColors.color.background,
            sheetContent = {
                when (uiState.sheetContentTypes) {
                    BottomSheetContentType.Times -> {
                        TimesSheetContent(
                            onShowTimer = { timer ->
                                showSheetWithoutLagging(scope, bottomSheetScaffoldState)
                                viewModel.setSheetContent(BottomSheetContentType.Progress)
                                viewModel.setTimer(timer)
                            },
                        )
                    }

                    BottomSheetContentType.Progress -> {
                        ProgressBottomSheetContent(
                            counter = uiState.minAndSec,
                            progress = uiState.progress,
                            onClickStop = {
                                scope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.hide()
                                    viewModel.setTimer(timer = 0, isResound = false)
                                    viewModel.setSheetContent(BottomSheetContentType.Times)
                                }
                            }
                        )
                    }
                }
            },
            content = { paddingValues ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        MainTopBar(onClick = viewModel::resetSound)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp)
                                .padding(paddingValues)
                        ) {
                            items(key = { index -> index.id }, items = uiState.sounds) { item ->
                                SoundItem(
                                    image = item.image,
                                    value = item.value,
                                    isPlayer = uiState.selectList.contains(item),
                                    onClick = {
                                        viewModel.onClickButton(item, context)
                                    },
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    AnimatedVisibility(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                        visible = uiState.selectList.isNotEmpty()
                    ) {
                        IconButton(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(AppColors.color.selectedColor)
                                .padding(4.dp),
                            onClick = {
                                if (uiState.minAndSec.isNotEmpty()) {
                                    viewModel.setSheetContent(BottomSheetContentType.Progress)
                                } else viewModel.setSheetContent(BottomSheetContentType.Times)
                                scope.launch {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.timer),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun showSheetWithoutLagging(
    scope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    scope.launch {
        bottomSheetScaffoldState.bottomSheetState.hide()
        bottomSheetScaffoldState.bottomSheetState.expand()
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
fun TimesSheetContent(
    onShowTimer: (time: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .background(AppColors.color.background)
    ) {
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
                time = 1,
                onClick = {
                    onShowTimer(1)
                }
            )
            TimerItem(
                modifier = Modifier.weight(1f),
                time = 15,
                onClick = {
                    onShowTimer(15)
                }
            )
            TimerItem(
                modifier = Modifier.weight(1f),
                time = 30,
                onClick = {
                    onShowTimer(30)
                }
            )
            TimerItem(
                modifier = Modifier.weight(1f),
                time = 60,
                onClick = {
                    onShowTimer(60)
                }
            )
        }
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

@Composable
fun ProgressBottomSheetContent(
    counter: String,
    progress: Float,
    onClickStop: () -> Unit
) {
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .height(270.dp)
            .background(AppColors.color.background)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.TopEnd)
                .clickable { onClickStop() }
                .padding(4.dp),
            imageVector = Icons.Default.Close,
            tint = AppColors.color.white,
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(250.dp),
                color = AppColors.color.selectedColor,
                progress = { progress },
                strokeCap = LinearStrokeCap
            )
        }
        Text(text = counter, color = AppColors.color.selectedColor)
    }
}