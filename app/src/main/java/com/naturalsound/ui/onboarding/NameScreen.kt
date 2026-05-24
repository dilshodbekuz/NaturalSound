package com.naturalsound.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naturalsound.ui.theme.*

@Composable
fun NameScreen(
    onDone: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalStrings.current
    val c = LocalAppColors.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.bgDeep)
            .statusBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        Text("👤", fontSize = 52.sp)
        Spacer(Modifier.height(16.dp))
        Text(strings.nameTitle, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.textPrimary)
        Spacer(Modifier.height(6.dp))
        Text(strings.nameSubtitle, fontSize = 14.sp, color = c.textMuted)

        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = s.name,
            onValueChange = viewModel::setName,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboard?.hide()
                viewModel.saveAndFinish(onDone)
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = Indigo,
                unfocusedBorderColor    = c.bgBorder,
                focusedTextColor        = c.textPrimary,
                unfocusedTextColor      = c.textPrimary,
                cursorColor             = Indigo,
                focusedContainerColor   = c.bgCard,
                unfocusedContainerColor = c.bgCard,
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { keyboard?.hide(); viewModel.saveAndFinish(onDone) },
            enabled = s.name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = Indigo,
                disabledContainerColor = c.bgBorder
            )
        ) {
            Text(strings.nameButton, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Spacer(Modifier.height(32.dp))
    }
}
