package com.example.naturalsound.main

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults.LinearStrokeCap
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.naturalsound.R
import com.example.naturalsound.composable.Spacer4
import com.example.naturalsound.composable.Text16spBold
import com.example.naturalsound.composable.Text24spBold
import com.example.naturalsound.composable.snowfall
import com.example.naturalsound.ui.theme.AppColors
import com.example.naturalsound.utils.Constants
import com.example.naturalsound.utils.LanguageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.min

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val preferences = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    val currentLanguage = preferences.getString(Constants.LANGUAGE_KEY, Constants.Languages.ENG)
    val scope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    BackHandler(enabled = bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch {
            bottomSheetScaffoldState.bottomSheetState.hide()
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
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.color.darkColor
                    ),
                    title = { Text24spBold(text = stringResource(R.string.app_name_title)) },
                    actions = {
                        AppbarActionsView(
                            expandedState = uiState.menuVisibility,
                            currentLanguage = currentLanguage!!,
                            onMenuItemClick = { languageCode ->
                                preferences.edit().putString(Constants.LANGUAGE_KEY, languageCode).apply()
                                LanguageHelper.changeLanguage(
                                    context = context,
                                    resources = context.resources,
                                    languageCode = languageCode
                                )
                                viewModel.onMenuVisibilityChange(
                                    uiState.menuVisibility.not()
                                )
                            },
                            onDismissMenu = { viewModel.onMenuVisibilityChange(uiState.menuVisibility.not()) },
                            onMenuClick = {
                                viewModel.onMenuVisibilityChange(uiState.menuVisibility.not())
                            }
                        )
                    }
                )
            },
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

@Composable
fun AppbarActionsView(
    onMenuClick: () -> Unit,
    expandedState: Boolean = false,
    onDismissMenu: () -> Unit,
    onMenuItemClick: (String) -> Unit,
    currentLanguage: String
) {
    IconButton(onClick = onMenuClick) {
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = if (expandedState) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            tint = AppColors.color.white,
            contentDescription = null
        )
    }
    LanguageMenuItems(
        expandedState = expandedState,
        onDismiss = onDismissMenu,
        onItemClick = onMenuItemClick,
        currentLanguage = currentLanguage
    )
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
fun SoundItem(image: Int?, value: Int, isPlayer: Boolean, onClick: () -> Unit) {
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
            text = stringResource(value),
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

@Composable
fun LanguageMenuItems(
    onItemClick: (String) -> Unit,
    expandedState: Boolean = false,
    onDismiss: () -> Unit = {},
    currentLanguage: String
) {
    DropdownMenu(
        modifier = Modifier.background(Color.Transparent),
        expanded = expandedState,
        onDismissRequest = onDismiss
    ) {
        LanguageMenuItem(
            onClick = { onItemClick(Constants.Languages.ENG) },
            text = stringResource(R.string.english),
            isChoosen = currentLanguage == Constants.Languages.ENG
        )
        LanguageMenuItem(
            onClick = { onItemClick(Constants.Languages.RUS) },
            text = stringResource(R.string.russian),
            isChoosen = currentLanguage == Constants.Languages.RUS
        )
        LanguageMenuItem(
            onClick = { onItemClick(Constants.Languages.UZB) },
            text = stringResource(R.string.uzbek),
            isChoosen = currentLanguage == Constants.Languages.UZB
        )
    }
}

@Composable
fun LanguageMenuItem(
    onClick: () -> Unit,
    text: String,
    isChoosen: Boolean = false
) {
    Row(
        modifier = Modifier
            .widthIn(min = 130.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, color = AppColors.color.darkColor, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        if (isChoosen) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AppColors.color.darkColor
            )
        }
    }
}