package com.time.applauncher.addict.feature.settings.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.StillScreenHeader
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun DistractingAppsRoot(
    onBack: () -> Unit,
    viewModel: DistractingAppsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DistractingAppsScreen(state = state, onBack = onBack, onAction = viewModel::onAction)
}

@Composable
fun DistractingAppsScreen(
    state: DistractingAppsState,
    onBack: () -> Unit,
    onAction: (DistractingAppsAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 20.dp)
    ) {
        StillScreenHeader(title = "Distracting apps", onAction = onBack)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonoLabel("OPEN WITH INTENTION", color = StillColors.TextTertiary, letterSpacing = 0.14.em)
            MonoLabel("${state.gatedCount} GATED", color = StillColors.TextTertiary, letterSpacing = 0.14.em)
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.apps, key = { it.packageName }) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple { onAction(DistractingAppsAction.OnToggleApp(app.packageName)) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = app.label,
                        style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, color = StillColors.TextPrimary)
                    )
                    GateSwitch(app.gated)
                }
            }
        }
    }
}

@Composable
private fun GateSwitch(checked: Boolean) {
    val knobOffset by animateDpAsState(if (checked) 21.dp else 3.dp, label = "gateKnob")
    Box(
        modifier = Modifier
            .width(42.dp)
            .height(24.dp)
            .background(
                if (checked) StillColors.Accent else StillColors.TrackOff,
                RoundedCornerShape(14.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 3.dp)
                .size(18.dp)
                .background(if (checked) StillColors.OnAccent else StillColors.TextStrong, CircleShape)
        )
    }
}

@Preview
@Composable
private fun DistractingAppsPreview() {
    StillTheme {
        DistractingAppsScreen(
            state = DistractingAppsState(
                apps = listOf(
                    DistractingAppUi("a", "Instagram", true),
                    DistractingAppUi("b", "YouTube", true),
                    DistractingAppUi("c", "Maps", false)
                )
            ),
            onBack = {},
            onAction = {}
        )
    }
}
