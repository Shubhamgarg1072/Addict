package com.time.applauncher.addict.feature.onboarding.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.breathe
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.component.pulse
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashRoot(
    onGoHome: () -> Unit,
    onGoOnboarding: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            SplashEvent.GoHome -> onGoHome()
            SplashEvent.GoOnboarding -> onGoOnboarding()
        }
    }
    SplashScreen(onTap = { viewModel.onAction(SplashAction.OnTap) })
}

@Composable
fun SplashScreen(onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .clickableNoRipple(onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .breathe()
                    .background(Color.White, CircleShape)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyleWhite) { append("Still") }
                    withStyle(SpanStyleDim) { append(".") }
                },
                style = TextStyle(fontFamily = Manrope, fontSize = 34.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
            )
            MonoLabel(
                text = "RECLAIM YOUR ATTENTION",
                color = StillColors.TextSecondary,
                fontSize = 11.sp,
                letterSpacing = 0.24.em
            )
        }
        MonoLabel(
            text = "TAP TO BEGIN",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
                .pulse(),
            color = StillColors.TextFaint,
            fontSize = 10.sp,
            letterSpacing = 0.2.em
        )
    }
}

private val SpanStyleWhite = androidx.compose.ui.text.SpanStyle(color = Color.White)
private val SpanStyleDim = androidx.compose.ui.text.SpanStyle(color = Color.White.copy(alpha = 0.35f))

@Preview
@Composable
private fun SplashPreview() {
    StillTheme { SplashScreen(onTap = {}) }
}
