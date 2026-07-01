package com.time.applauncher.addict.feature.wellbeing.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.time.applauncher.addict.core.designsystem.component.StillScreenHeader
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardRoot(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardScreen(state = state, onBack = onBack)
}

@Composable
fun DashboardScreen(state: DashboardState, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 30.dp)
    ) {
        StillScreenHeader(title = "Screen time", onAction = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = state.screenTime,
                    style = TextStyle(fontFamily = Manrope, fontSize = 52.sp, fontWeight = FontWeight.Light, color = Color.White, letterSpacing = (-0.03).em)
                )
                MonoLabel("TODAY", modifier = Modifier.padding(bottom = 10.dp), color = StillColors.TextTertiary, fontSize = 11.sp)
            }
            MonoLabel(state.delta, modifier = Modifier.padding(top = 4.dp), color = StillColors.TextTertiary, fontSize = 12.sp, letterSpacing = 0.02.em)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(top = 34.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.bars.forEach { bar -> WeekBar(bar, Modifier.weight(1f)) }
            }

            Row(modifier = Modifier.padding(top = 30.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("DAILY AVG", state.dailyAvg, Modifier.weight(1f))
                StatCard("FOCUS STREAK", state.streak, Modifier.weight(1f))
            }

            MonoLabel("MOST OPENED", modifier = Modifier.padding(top = 30.dp, bottom = 14.dp), color = StillColors.TextTertiary)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 20.dp)) {
                state.topApps.forEach { app -> TopAppRow(app) }
            }
        }
    }
}

@Composable
private fun WeekBar(bar: WeekBarUi, modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .fillMaxHeight(bar.fraction.coerceAtLeast(0.03f))
                .background(
                    if (bar.isToday) Color.White else Color.White.copy(alpha = 0.22f),
                    RoundedCornerShape(4.dp)
                )
        )
        Text(
            text = bar.label,
            modifier = Modifier.padding(top = 9.dp),
            style = TextStyle(
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                color = if (bar.isToday) StillColors.TextSecondary else StillColors.TextTertiary
            )
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(StillColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, StillColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        MonoLabel(label, color = StillColors.TextTertiary, fontSize = 10.sp, letterSpacing = 0.12.em)
        Text(
            text = value,
            modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(fontFamily = Manrope, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        )
    }
}

@Composable
private fun TopAppRow(app: TopAppUi) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(app.name, style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = StillColors.TextPrimary))
            Text(app.time, style = TextStyle(fontFamily = JetBrainsMono, fontSize = 13.sp, color = StillColors.TextSecondary))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(app.fraction)
                    .height(3.dp)
                    .background(Color.White.copy(alpha = 0.55f), RoundedCornerShape(2.dp))
            )
        }
    }
}

@Preview
@Composable
private fun DashboardPreview() {
    StillTheme {
        DashboardScreen(
            state = DashboardState(
                screenTime = "2h 18m",
                delta = "↓ 22% vs last week",
                bars = listOf(
                    WeekBarUi("M", 0.7f, false), WeekBarUi("T", 0.6f, false), WeekBarUi("W", 1f, true)
                ),
                dailyAvg = "2h 44m",
                topApps = listOf(TopAppUi("Instagram", "1h 12m", 1f), TopAppUi("YouTube", "34m", 0.5f))
            ),
            onBack = {}
        )
    }
}
