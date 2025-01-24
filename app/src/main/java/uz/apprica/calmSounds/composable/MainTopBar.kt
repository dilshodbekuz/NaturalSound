package uz.apprica.calmSounds.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.apprica.calmSounds.ui.theme.AppColors

@Composable
fun MainTopBar(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Natural Sound",
                color = AppColors.color.textColor,
                fontWeight = FontWeight.W700,
                fontSize = 24.sp
            )
//            Spacer(modifier = Modifier.weight(1f))
//            IconButton(onClick = onClick) {
//                Icon(
//                    imageVector = Icons.Default.Close,
//                    contentDescription = null,
//                    tint = Color.White
//                )
//            }
        }
        Spacer4()
    }
}