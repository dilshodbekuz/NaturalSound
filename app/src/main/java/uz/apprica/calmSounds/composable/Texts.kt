package uz.apprica.calmSounds.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import uz.apprica.calmSounds.ui.theme.AppColors

@Composable
fun Text16spBold(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.W700,
        color = AppColors.color.textColor
    )
}

@Composable
fun Text18spBold(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.W700,
        color = AppColors.color.textColor
    )
}

@Composable
fun Text24spBold(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppColors.color.textColor,
    textAlign: TextAlign = TextAlign.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        fontSize = 24.sp,
        fontWeight = FontWeight.W700,
        color = color
    )
}