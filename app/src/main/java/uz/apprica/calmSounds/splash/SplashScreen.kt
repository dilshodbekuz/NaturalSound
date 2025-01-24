package uz.apprica.calmSounds.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uz.apprica.calmSounds.composable.Spacer16
import uz.apprica.calmSounds.composable.Text24spBold
import uz.apprica.calmSounds.composable.snowfall
import uz.apprica.calmSounds.ui.theme.AppColors
import kotlinx.coroutines.delay
import uz.apprica.calmSounds.R
import kotlin.random.Random

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate("home") {
            popUpTo(route = "splash") {
                inclusive = true
            }
        }
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
        Text24spBold(text = stringResource(R.string.app_name))
    }
}

data class Snowflake(
    var x: Float,
    var y: Float,
    var radius: Float,
    var speed: Float
)

@Composable
fun SnowfallEffect() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .snowfall()
    ) {

    }
//    val snowflakes = remember { List(100) { generateRandomSnowflake() } }
//    val infiniteTransition = rememberInfiniteTransition(label = "")
//
//    val offsetY by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1000f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 50000, easing = LinearEasing),
//            initialStartOffset = StartOffset(10)
//        ), label = ""
//    )
//
//    Canvas(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    ) {
//        snowflakes.forEach { snowflake ->
//            drawSnowflake(snowflake, offsetY)
//        }
//    }
}

fun generateRandomSnowflake(): Snowflake {
    return Snowflake(
        x = Random.nextFloat(),
        y = Random.nextFloat() * 10000f,
        radius = Random.nextFloat() * 4f + 2f, // Snowflake size
        speed = Random.nextFloat() * 2f + 1f  // Falling speed
    )
}

fun DrawScope.drawSnowflake(snowflake: Snowflake, offsetY: Float) {
    val newY = (snowflake.y + offsetY * snowflake.speed) % size.height
    drawCircle(
        Color.White,
        radius = snowflake.radius,
        center = Offset(snowflake.x * size.width, newY)
    )
}
