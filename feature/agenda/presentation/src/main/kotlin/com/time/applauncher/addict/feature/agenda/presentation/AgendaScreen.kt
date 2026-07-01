package com.time.applauncher.addict.feature.agenda.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
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
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.feature.agenda.domain.AgendaDay
import com.time.applauncher.addict.feature.agenda.domain.AgendaEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun AgendaRoot(
    onBack: () -> Unit,
    viewModel: AgendaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AgendaScreen(days = state, onBack = onBack)
}

@Composable
fun AgendaScreen(days: List<AgendaDay>, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 34.dp)
    ) {
        ScreenHeader(title = "Agenda", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            days.forEach { group ->
                MonoLabel(
                    text = group.day,
                    modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
                    color = StillColors.TextTertiary,
                    letterSpacing = 0.16.em
                )
                group.events.forEach { event -> AgendaRow(event) }
                Box(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun AgendaRow(event: AgendaEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = event.time,
            modifier = Modifier.width(48.dp),
            style = TextStyle(fontFamily = JetBrainsMono, fontSize = 13.sp, color = StillColors.TextSecondary)
        )
        Column {
            Text(
                text = event.title,
                style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = StillColors.TextPrimary)
            )
            Text(
                text = event.meta,
                style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = StillColors.TextTertiary)
            )
        }
    }
}

@Composable
internal fun ScreenHeader(title: String, onBack: () -> Unit, actionLabel: String = "DONE") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = TextStyle(fontFamily = Manrope, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.01).em)
        )
        MonoLabel(
            text = actionLabel,
            modifier = Modifier.clickableNoRipple(onClick = onBack),
            color = StillColors.TextSecondary,
            fontSize = 11.sp,
            letterSpacing = 0.1.em
        )
    }
    Box(Modifier.height(12.dp))
}

@Preview
@Composable
private fun AgendaPreview() {
    StillTheme {
        AgendaScreen(
            days = listOf(
                AgendaDay("TODAY", listOf(AgendaEvent("09:30", "Design review", "45 min · Room 3")))
            ),
            onBack = {}
        )
    }
}
