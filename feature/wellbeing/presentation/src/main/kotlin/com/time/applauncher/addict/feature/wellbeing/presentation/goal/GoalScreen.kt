package com.time.applauncher.addict.feature.wellbeing.presentation.goal

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.time.applauncher.addict.feature.wellbeing.presentation.minutesToHm
import org.koin.androidx.compose.koinViewModel

@Composable
fun GoalRoot(
    onBack: () -> Unit,
    viewModel: GoalViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    GoalScreen(state = state, onBack = onBack, onAction = viewModel::onAction)
}

@Composable
fun GoalScreen(
    state: GoalState,
    onBack: () -> Unit,
    onAction: (GoalAction) -> Unit
) {
    val used = state.usedMinutes
    val limit = state.limitMinutes
    val pct = (used.toFloat() / limit).coerceIn(0f, 1f)
    val overGoal = used > limit
    val remaining = (limit - used).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 34.dp)
    ) {
        StillScreenHeader(title = "Daily goal", onAction = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.padding(top = 24.dp).size(210.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(210.dp)) {
                    val stroke = 18.dp.toPx()
                    val inset = stroke / 2
                    val arcSize = Size(size.width - stroke, size.height - stroke)
                    drawArc(
                        color = StillColors.Ink(0.12f),
                        startAngle = -90f, sweepAngle = 360f, useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                        size = arcSize, style = Stroke(width = stroke)
                    )
                    drawArc(
                        color = if (overGoal) StillColors.Ink(0.5f) else StillColors.Accent,
                        startAngle = -90f, sweepAngle = 360f * pct, useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                        size = arcSize, style = Stroke(width = stroke)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = minutesToHm(used),
                        style = TextStyle(fontFamily = Manrope, fontSize = 40.sp, fontWeight = FontWeight.Light, color = StillColors.TextStrong, letterSpacing = (-0.02).em)
                    )
                    MonoLabel("OF ${minutesToHm(limit)}", modifier = Modifier.padding(top = 4.dp), color = StillColors.TextSecondary, fontSize = 10.sp, letterSpacing = 0.14.em)
                }
            }
            Text(
                text = goalMessage(overGoal, pct, remaining),
                modifier = Modifier.padding(top = 26.dp, start = 16.dp, end = 16.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, lineHeight = 22.sp, color = StillColors.TextSecondary)
            )
        }

        MonoLabel("SET LIMIT", modifier = Modifier.padding(bottom = 12.dp), color = StillColors.TextTertiary, letterSpacing = 0.16.em)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(120, 150, 180, 240).forEach { minutes ->
                GoalChip(
                    label = chipLabel(minutes),
                    selected = limit == minutes,
                    modifier = Modifier.weight(1f)
                ) { onAction(GoalAction.OnSelectLimit(minutes)) }
            }
        }
    }
}

@Composable
private fun GoalChip(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(if (selected) StillColors.Accent else Color.Transparent, RoundedCornerShape(22.dp))
            .border(1.dp, if (selected) StillColors.Accent else StillColors.BorderStrong, RoundedCornerShape(22.dp))
            .clickableNoRipple(onClick = onClick)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (selected) StillColors.OnAccent else StillColors.TextSecondary)
        )
    }
}

private fun chipLabel(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (m > 0) "${h}h$m" else "${h}h"
}

private fun goalMessage(over: Boolean, pct: Float, remaining: Int): String = when {
    over -> "You’re past today’s limit. Be gentle — tomorrow resets."
    pct > 0.8f -> "Almost at your limit. Make the rest count."
    else -> "On track. ${minutesToHm(remaining)} of intentional time left."
}

@Preview
@Composable
private fun GoalPreview() {
    StillTheme { GoalScreen(state = GoalState(usedMinutes = 138, limitMinutes = 150), onBack = {}, onAction = {}) }
}
