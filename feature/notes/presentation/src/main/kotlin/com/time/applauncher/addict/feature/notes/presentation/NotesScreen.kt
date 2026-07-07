package com.time.applauncher.addict.feature.notes.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.StillScreenHeader
import com.time.applauncher.addict.core.designsystem.component.StillTextField
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.component.dashedBorder
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.feature.notes.domain.ChecklistItem
import com.time.applauncher.addict.feature.notes.domain.Note
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotesRoot(
    onBack: () -> Unit,
    viewModel: NotesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NotesScreen(state = state, onBack = onBack, onAction = viewModel::onAction)
}

@Composable
fun NotesScreen(
    state: NotesState,
    onBack: () -> Unit,
    onAction: (NotesAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 34.dp)
    ) {
        StillScreenHeader(title = "Notes", onAction = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // NOTES
            MonoLabel("NOTES", color = StillColors.TextTertiary)
            if (state.notes.isEmpty() && !state.isEditing) {
                Text(
                    text = "Capture a thought before it captures you.",
                    style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, color = StillColors.TextTertiary)
                )
            }
            state.notes.forEach { note -> NoteCard(note, state, onAction) }
            if (state.isEditing) {
                NoteEditor(state = state, onAction = onAction)
            } else {
                AddTile("+ New note") { onAction(NotesAction.OnAddNote) }
            }

            Spacer(Modifier.height(10.dp))

            // CHALLENGES
            MonoLabel("CHALLENGES", color = StillColors.TextTertiary)
            if (state.challenges.isEmpty() && !state.isSettingChallenge) {
                Text(
                    text = "Set a daily habit and watch the streak grow. It shows up on your home screen.",
                    style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, lineHeight = 20.sp, color = StillColors.TextTertiary)
                )
            }
            state.challenges.forEach { challenge -> ChallengeCard(challenge, onAction) }
            if (state.isSettingChallenge) {
                ChallengeEditor(state = state, onAction = onAction)
            } else {
                AddTile("+ Set a challenge") { onAction(NotesAction.OnSetChallenge) }
            }
        }
    }
}

@Composable
private fun AddTile(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dashedBorder(StillColors.TrackOff, RoundedCornerShape(16.dp))
            .clickableNoRipple(onClick = onClick)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = StillColors.TextSecondary)
        )
    }
}

@Composable
private fun NoteEditor(state: NotesState, onAction: (NotesAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StillColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, StillColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MonoLabel("NEW NOTE", color = StillColors.TextTertiary)
        StillTextField(
            value = state.draftTitle,
            onValueChange = { onAction(NotesAction.OnDraftTitleChange(it)) },
            placeholder = "Title"
        )
        StillTextField(
            value = state.draftBody,
            onValueChange = { onAction(NotesAction.OnDraftBodyChange(it)) },
            placeholder = "Write something — optional",
            singleLine = false
        )
        EditorButtons(
            canSave = state.canSaveNote,
            onCancel = { onAction(NotesAction.OnDismissEditor) },
            onSave = { onAction(NotesAction.OnSaveNote) }
        )
    }
}

@Composable
private fun ChallengeEditor(state: NotesState, onAction: (NotesAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StillColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, StillColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MonoLabel("NEW CHALLENGE", color = StillColors.TextTertiary)
        StillTextField(
            value = state.challengeTitle,
            onValueChange = { onAction(NotesAction.OnChallengeTitleChange(it)) },
            placeholder = "Habit — e.g. Read 20 pages"
        )
        StillTextField(
            value = state.challengeTarget,
            onValueChange = { onAction(NotesAction.OnChallengeTargetChange(it)) },
            placeholder = "Target days — default 30"
        )
        EditorButtons(
            canSave = state.canSaveChallenge,
            onCancel = { onAction(NotesAction.OnDismissChallengeEditor) },
            onSave = { onAction(NotesAction.OnSaveChallenge) }
        )
    }
}

@Composable
private fun EditorButtons(canSave: Boolean, onCancel: () -> Unit, onSave: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(24.dp))
                .clickableNoRipple(onClick = onCancel)
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
                .background(if (canSave) StillColors.Accent else StillColors.TrackOff, RoundedCornerShape(24.dp))
                .clickableNoRipple(onClick = onSave)
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Save",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (canSave) StillColors.OnAccent else StillColors.TextSecondary
                )
            )
        }
    }
}

@Composable
private fun NoteCard(note: Note, state: NotesState, onAction: (NotesAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StillColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, StillColors.Border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (note.pinned) {
                Box(Modifier.size(5.dp).background(StillColors.Accent, CircleShape))
            }
            Text(
                text = note.title,
                modifier = Modifier.weight(1f),
                style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextStrong)
            )
            MonoLabel(
                text = "×",
                modifier = Modifier
                    .clickableNoRipple { onAction(NotesAction.OnDeleteNote(note.id)) }
                    .padding(horizontal = 6.dp),
                color = StillColors.TextTertiary,
                fontSize = 16.sp
            )
        }
        if (note.items.isNotEmpty()) Box(Modifier.height(10.dp))
        note.items.forEach { item -> ChecklistRow(item, onAction) }
        if (note.body != null) {
            Text(
                text = note.body!!,
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, lineHeight = 22.sp, color = StillColors.TextSecondary)
            )
        }
        if (state.addingItemNoteId == note.id) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StillTextField(
                        value = state.draftItem,
                        onValueChange = { onAction(NotesAction.OnDraftItemChange(it)) },
                        placeholder = "Checklist item"
                    )
                }
                MonoLabel(
                    text = "ADD",
                    modifier = Modifier
                        .clickableNoRipple { onAction(NotesAction.OnSaveItem) }
                        .padding(6.dp),
                    color = if (state.canSaveItem) StillColors.TextPrimary else StillColors.TextTertiary,
                    fontSize = 11.sp,
                    letterSpacing = 0.12.em
                )
            }
        } else {
            Text(
                text = "+ item",
                modifier = Modifier
                    .clickableNoRipple { onAction(NotesAction.OnStartAddItem(note.id)) }
                    .padding(top = 8.dp),
                style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = StillColors.TextTertiary)
            )
        }
    }
}

@Composable
private fun ChecklistRow(item: ChecklistItem, onAction: (NotesAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple { onAction(NotesAction.OnToggleItem(item.id, !item.done)) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .background(if (item.done) StillColors.Accent else Color.Transparent, RoundedCornerShape(4.dp))
                .border(
                    1.5.dp,
                    if (item.done) StillColors.Accent else StillColors.TextTertiary,
                    RoundedCornerShape(4.dp)
                )
        )
        Text(
            text = item.text,
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 14.sp,
                color = if (item.done) StillColors.TextTertiary else StillColors.TextPrimary,
                textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }
}

@Composable
private fun ChallengeCard(challenge: ChallengeUi, onAction: (NotesAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StillColors.Surface, RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (challenge.doneToday) StillColors.BorderStrong else StillColors.Border,
                RoundedCornerShape(18.dp)
            )
            .padding(18.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = TextStyle(fontFamily = Manrope, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextStrong, letterSpacing = (-0.01).em)
                )
                MonoLabel(challenge.goalLabel, color = StillColors.TextSecondary, letterSpacing = 0.1.em, modifier = Modifier.padding(top = 5.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = challenge.streak.toString(),
                    style = TextStyle(fontFamily = Manrope, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = StillColors.TextStrong)
                )
                MonoLabel("DAY STREAK", color = StillColors.TextSecondary, fontSize = 9.sp, letterSpacing = 0.14.em, modifier = Modifier.padding(top = 4.dp))
            }
            MonoLabel(
                text = "×",
                modifier = Modifier
                    .clickableNoRipple { onAction(NotesAction.OnDeleteChallenge(challenge.id)) }
                    .padding(start = 4.dp),
                color = StillColors.TextTertiary,
                fontSize = 16.sp
            )
        }

        Box(
            modifier = Modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth()
                .height(3.dp)
                .background(StillColors.Ink(0.08f), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(challenge.progress)
                    .height(3.dp)
                    .background(StillColors.Accent, RoundedCornerShape(2.dp))
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            challenge.week.forEach { day -> DayCell(day) }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            ReminderButton(challenge, onAction)
            Spacer(Modifier.weight(1f))
            MarkTodayButton(challenge, onAction)
        }
    }
}

@Composable
private fun DayCell(day: DayCellUi) {
    val dotColor = when {
        day.done -> StillColors.Accent
        day.isToday -> StillColors.BorderSelected
        else -> StillColors.Ink(0.16f)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Box(
            modifier = Modifier
                .size(11.dp)
                .then(
                    if (day.done) Modifier.background(dotColor, CircleShape)
                    else Modifier.border(1.5.dp, dotColor, CircleShape)
                )
        )
        MonoLabel(
            day.label,
            color = if (day.isToday) StillColors.Ink(0.7f) else StillColors.TextTertiary,
            fontSize = 9.sp,
            letterSpacing = 0.05.em
        )
    }
}

@Composable
private fun ReminderButton(challenge: ChallengeUi, onAction: (NotesAction) -> Unit) {
    Row(
        modifier = Modifier
            .border(
                1.dp,
                if (challenge.hasReminder) StillColors.Ink(0.22f) else StillColors.BorderStrong,
                RoundedCornerShape(20.dp)
            )
            .clickableNoRipple { onAction(NotesAction.OnCycleReminder(challenge.id)) }
            .padding(horizontal = 13.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .size(6.dp)
                .background(if (challenge.hasReminder) StillColors.Accent else StillColors.TextTertiary, CircleShape)
        )
        MonoLabel(
            challenge.reminderLabel,
            color = if (challenge.hasReminder) StillColors.TextPrimary else StillColors.TextTertiary,
            fontSize = 11.sp,
            letterSpacing = 0.04.em
        )
    }
}

@Composable
private fun MarkTodayButton(challenge: ChallengeUi, onAction: (NotesAction) -> Unit) {
    val done = challenge.doneToday
    Box(
        modifier = Modifier
            .then(
                if (done) Modifier.background(StillColors.Accent, RoundedCornerShape(22.dp))
                else Modifier.border(1.dp, StillColors.BorderStrong, RoundedCornerShape(22.dp))
            )
            .clickableNoRipple { onAction(NotesAction.OnMarkChallengeToday(challenge.id, !done)) }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (done) "Done ✓" else "Mark today",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (done) StillColors.OnAccent else StillColors.TextStrong
            )
        )
    }
}

@Preview
@Composable
private fun NotesPreview() {
    StillTheme {
        val week = listOf(true, true, true, false, true, true, true)
        NotesScreen(
            state = NotesState(
                notes = listOf(
                    Note(1, "Reading list", true, null, listOf(ChecklistItem(1, "Deep Work", true), ChecklistItem(2, "The Shallows", false))),
                    Note(2, "Idea", false, "A phone that asks why.", emptyList())
                ),
                challenges = listOf(
                    ChallengeUi(
                        id = 1, title = "Read 20 pages", goalLabel = "GOAL · 30 DAYS", streak = 7,
                        progress = 0.23f, doneToday = true,
                        week = week.mapIndexed { i, d -> DayCellUi(listOf("S","M","T","W","T","F","S")[i], d, i == 6) },
                        reminderLabel = "Remind 21:00", hasReminder = true
                    )
                )
            ),
            onBack = {},
            onAction = {}
        )
    }
}
