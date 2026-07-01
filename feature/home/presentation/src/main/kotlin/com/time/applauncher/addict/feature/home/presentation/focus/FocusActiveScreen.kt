package com.time.applauncher.addict.feature.home.presentation.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.breathe
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val ALLOWED = listOf("PHONE", "MESSAGES", "CAMERA", "CLOCK")

@Composable
fun FocusActiveRoot(
    minutes: Int,
    onGoHome: () -> Unit,
    viewModel: FocusActiveViewModel = koinViewModel { parametersOf(minutes) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            FocusActiveEvent.GoHome -> onGoHome()
        }
    }
    FocusActiveScreen(state = state, onEnd = { viewModel.onAction(FocusActiveAction.OnEndFocus) })
}

@Composable
fun FocusActiveScreen(state: FocusActiveState, onEnd: () -> Unit) {
    val mm = state.remainingSeconds / 60
    val ss = state.remainingSeconds % 60
    val clock = "$mm:${if (ss < 10) "0$ss" else "$ss"}"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .size(16.dp)
                    .breathe(4000)
                    .background(Color.White, CircleShape)
            )
            MonoLabel("IN FOCUS", color = StillColors.TextSecondary, fontSize = 11.sp, letterSpacing = 0.24.em)
            Text(
                text = clock,
                modifier = Modifier.padding(top = 14.dp, bottom = 6.dp),
                style = TextStyle(fontSize = 76.sp, fontWeight = FontWeight.Thin, color = Color.White, letterSpacing = (-0.02).em)
            )
            Text("remaining", style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, color = StillColors.TextTertiary))
            Row(modifier = Modifier.padding(top = 52.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ALLOWED.forEach { app ->
                    Text(app, style = TextStyle(fontFamily = JetBrainsMono, fontSize = 11.sp, color = StillColors.TextSecondary, letterSpacing = 0.06.em))
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 54.dp)
                .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(26.dp))
                .clickableNoRipple(onClick = onEnd)
                .padding(horizontal = 30.dp, vertical = 12.dp)
        ) {
            Text("End focus", style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextSecondary))
        }
    }
}

@Preview
@Composable
private fun FocusActivePreview() {
    StillTheme { FocusActiveScreen(state = FocusActiveState(remainingSeconds = 754), onEnd = {}) }
}
