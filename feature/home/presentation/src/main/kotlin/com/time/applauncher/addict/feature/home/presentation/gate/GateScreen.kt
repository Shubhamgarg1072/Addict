package com.time.applauncher.addict.feature.home.presentation.gate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GateRoot(
    packageName: String,
    appLabel: String,
    onGoHome: () -> Unit,
    viewModel: GateViewModel = koinViewModel { parametersOf(packageName, appLabel) }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GateEvent.GoHome -> onGoHome()
        }
    }
    GateScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun GateScreen(state: GateState, onAction: (GateAction) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 34.dp),
        contentAlignment = Alignment.Center
    ) {
        if (state.countdown == null) {
            GateReasons(state, onAction)
        } else {
            Countdown(state.countdown, state.appLabel, onCancel = { onAction(GateAction.OnCancel) })
        }
    }
}

@Composable
private fun GateReasons(state: GateState, onAction: (GateAction) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MonoLabel("YOU'VE ALREADY SPENT", color = StillColors.TextSecondary, fontSize = 11.sp, letterSpacing = 0.18.em)
        Text(
            text = state.spent,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            style = TextStyle(fontSize = 56.sp, fontWeight = FontWeight.Light, color = Color.White, letterSpacing = (-0.03).em)
        )
        MonoLabel("HERE TODAY", color = StillColors.TextTertiary, fontSize = 11.sp, letterSpacing = 0.14.em)
        Text(
            text = "Why are you opening\n${state.appLabel}?",
            modifier = Modifier.padding(top = 34.dp, bottom = 20.dp),
            style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold, lineHeight = 34.sp, color = StillColors.TextPrimary)
        )
        Column(verticalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.padding(bottom = 30.dp)) {
            GATE_REASONS.forEachIndexed { index, reason ->
                val selected = state.selectedReason == index
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (selected) Color.White.copy(alpha = 0.08f) else StillColors.PanelDark, RoundedCornerShape(14.dp))
                        .border(1.dp, if (selected) StillColors.BorderSelected else StillColors.BorderStrong, RoundedCornerShape(14.dp))
                        .clickableNoRipple { onAction(GateAction.OnSelectReason(index)) }
                        .padding(horizontal = 18.dp, vertical = 15.dp)
                ) {
                    Text(
                        text = reason,
                        style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, color = if (selected) Color.White else StillColors.TextSecondary)
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedPill("Not now", Modifier.weight(1f)) { onAction(GateAction.OnNotNow) }
            FilledDimPill("Open anyway", Modifier.weight(1f)) { onAction(GateAction.OnOpenAnyway) }
        }
    }
}

@Composable
private fun Countdown(value: Int, appLabel: String, onCancel: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        MonoLabel("OPENING ${appLabel.uppercase()}", color = StillColors.TextSecondary, fontSize = 11.sp, letterSpacing = 0.2.em)
        Text(
            text = value.toString(),
            style = TextStyle(fontSize = 120.sp, fontWeight = FontWeight.Thin, color = Color.White)
        )
        MonoLabel(
            "CANCEL",
            modifier = Modifier.padding(top = 10.dp).clickableNoRipple(onClick = onCancel),
            color = StillColors.TextTertiary,
            fontSize = 11.sp,
            letterSpacing = 0.14.em
        )
    }
}

@Composable
private fun OutlinedPill(text: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(28.dp))
            .clickableNoRipple(onClick = onClick)
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White))
    }
}

@Composable
private fun FilledDimPill(text: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(28.dp))
            .clickableNoRipple(onClick = onClick)
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextSecondary))
    }
}

@Preview
@Composable
private fun GatePreview() {
    StillTheme {
        GateScreen(state = GateState(appLabel = "Instagram", spent = "2h 18m", selectedReason = 1), onAction = {})
    }
}
