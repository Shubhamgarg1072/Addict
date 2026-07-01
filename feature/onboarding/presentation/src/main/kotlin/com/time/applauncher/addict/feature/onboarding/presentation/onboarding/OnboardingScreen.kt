package com.time.applauncher.addict.feature.onboarding.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
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
import com.time.applauncher.addict.core.designsystem.component.StillPrimaryButton
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

private data class OnboardingPage(val kicker: String, val title: String, val body: String, val cta: String)

private val PAGES = listOf(
    OnboardingPage("01 / 03", "Reclaim your attention", "A launcher that gets out of the way. No icons to tempt you, no badges to chase.", "Next"),
    OnboardingPage("02 / 03", "Words, not icons", "Your apps live as quiet text. You reach for them on purpose — never by reflex.", "Next"),
    OnboardingPage("03 / 03", "Launch with intention", "Distracting apps pause before they open, so you decide — not the feed.", "Get started")
)

@Composable
fun OnboardingRoot(
    onGoPermission: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            OnboardingEvent.GoPermission -> onGoPermission()
        }
    }
    OnboardingScreen(step = state, onNext = { viewModel.onAction(OnboardingAction.OnNext) })
}

@Composable
fun OnboardingScreen(step: Int, onNext: () -> Unit) {
    val page = PAGES[step.coerceIn(0, PAGES.lastIndex)]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 34.dp, vertical = 44.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            MonoLabel(page.kicker, color = StillColors.TextSecondary, fontSize = 11.sp, letterSpacing = 0.2.em)
            Box(Modifier.height(20.dp))
            Text(
                text = page.title,
                style = TextStyle(fontFamily = Manrope, fontSize = 36.sp, lineHeight = 40.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.02).em)
            )
            Box(Modifier.height(18.dp))
            Text(
                text = page.body,
                style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, lineHeight = 26.sp, color = StillColors.TextSecondary)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.weight(1f)) {
                PAGES.indices.forEach { i ->
                    val width by animateDpAsState(if (i == step) 20.dp else 7.dp, label = "dot")
                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(7.dp)
                            .background(
                                if (i == step) Color.White else Color.White.copy(alpha = 0.25f),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
            StillPrimaryButton(text = page.cta, onClick = onNext)
        }
    }
}

@Preview
@Composable
private fun OnboardingPreview() {
    StillTheme { OnboardingScreen(step = 0, onNext = {}) }
}
