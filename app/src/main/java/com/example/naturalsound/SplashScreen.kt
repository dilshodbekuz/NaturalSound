package com.example.naturalsound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.naturalsound.composable.Spacer16
import com.example.naturalsound.composable.Text24spBold
import com.example.naturalsound.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1500)
        navController.navigate("home")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.color.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music_), // Replace with your app logo
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = AppColors.color.white
        )
        Spacer16()
        Text24spBold(text = "Natural Sounds")
    }
}