package com.time.applauncher.addict.feature.agenda.presentation

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
import com.time.applauncher.addict.core.designsystem.component.StillTextField
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
    AgendaScreen(state = state, onBack = onBack, onAction = viewModel::onAction)
}

@Composable
fun AgendaScreen(
    state: AgendaState,
    onBack: () -> Unit,
    onAction: (AgendaAction) -> Unit
) {
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
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (state.days.isEmpty() && !state.isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MonoLabel("NOTHING SCHEDULED", color = StillColors.TextTertiary, letterSpacing = 0.16.em)
                    Text(
                        text = "A clear day is a gift.",
                        modifier = Modifier.padding(top = 10.dp),
                        style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, color = StillColors.TextTertiary)
                    )
                }
            }
            state.days.forEach { group ->
                MonoLabel(
                    text = group.day,
                    modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
                    color = StillColors.TextTertiary,
                    letterSpacing = 0.16.em
                )
                group.events.forEach { event ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(StillColors.Border)
                    )
                    AgendaRow(event, onDelete = { onAction(AgendaAction.OnDeleteEvent(event.id)) })
                }
                Box(Modifier.height(20.dp))
            }
        }
        Box(Modifier.height(12.dp))
        if (state.isEditing) {
            EventEditor(state = state, onAction = onAction)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(16.dp))
                    .clickableNoRipple { onAction(AgendaAction.OnAddEvent) }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ New event",
                    style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = StillColors.TextSecondary)
                )
            }
        }
    }
}

@Composable
private fun EventEditor(state: AgendaState, onAction: (AgendaAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StillColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, StillColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MonoLabel("NEW EVENT", color = StillColors.TextTertiary, letterSpacing = 0.16.em)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AGENDA_DAY_OPTIONS.forEach { day ->
                val selected = state.draftDay == day
                MonoLabel(
                    text = day,
                    modifier = Modifier
                        .background(
                            if (selected) StillColors.Accent else Color.Transparent,
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            1.dp,
                            if (selected) StillColors.Accent else StillColors.BorderStrong,
                            RoundedCornerShape(20.dp)
                        )
                        .clickableNoRipple { onAction(AgendaAction.OnDraftDayChange(day)) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    color = if (selected) StillColors.OnAccent else StillColors.TextSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 0.12.em
                )
            }
        }
        StillTextField(
            value = state.draftTime,
            onValueChange = { onAction(AgendaAction.OnDraftTimeChange(it)) },
            placeholder = "Time — e.g. 09:30"
        )
        StillTextField(
            value = state.draftTitle,
            onValueChange = { onAction(AgendaAction.OnDraftTitleChange(it)) },
            placeholder = "Title"
        )
        StillTextField(
            value = state.draftMeta,
            onValueChange = { onAction(AgendaAction.OnDraftMetaChange(it)) },
            placeholder = "Details — optional"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(24.dp))
                    .clickableNoRipple { onAction(AgendaAction.OnDismissEditor) }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextPrimary)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (state.canSave) StillColors.Accent else StillColors.TrackOff,
                        RoundedCornerShape(24.dp)
                    )
                    .clickableNoRipple { onAction(AgendaAction.OnSaveEvent) }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Add",
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (state.canSave) StillColors.OnAccent else StillColors.TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun AgendaRow(event: AgendaEvent, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = event.time,
            modifier = Modifier.width(48.dp),
            style = TextStyle(fontFamily = JetBrainsMono, fontSize = 13.sp, color = StillColors.TextSecondary)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = StillColors.TextPrimary)
            )
            Text(
                text = event.meta,
                style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = StillColors.TextTertiary)
            )
        }
        MonoLabel(
            text = "×",
            modifier = Modifier
                .clickableNoRipple(onClick = onDelete)
                .padding(8.dp),
            color = StillColors.TextTertiary,
            fontSize = 16.sp
        )
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
            state = AgendaState(
                days = listOf(
                    AgendaDay("TODAY", listOf(AgendaEvent(1, "09:30", "Design review", "45 min · Room 3")))
                )
            ),
            onBack = {},
            onAction = {}
        )
    }
}
