package com.time.applauncher.addict.feature.notes.presentation

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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.StillScreenHeader
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
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
        Box(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.notes.forEach { note -> NoteCard(note, onAction) }
        }
        Box(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(16.dp))
                .clickableNoRipple { onAction(NotesAction.OnAddNote) }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+ New note",
                style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = StillColors.TextSecondary)
            )
        }
    }
}

@Composable
private fun NoteCard(note: Note, onAction: (NotesAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StillColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, StillColors.Border, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (note.pinned) {
                Box(Modifier.size(5.dp).background(Color.White, CircleShape))
            }
            Text(
                text = note.title,
                style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
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
                .background(
                    if (item.done) Color.White else Color.Transparent,
                    RoundedCornerShape(4.dp)
                )
                .border(
                    1.5.dp,
                    if (item.done) Color.White else StillColors.TextTertiary,
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

@Preview
@Composable
private fun NotesPreview() {
    StillTheme {
        NotesScreen(
            state = NotesState(
                notes = listOf(
                    Note(1, "Reading list", true, null, listOf(ChecklistItem(1, "Deep Work", true), ChecklistItem(2, "The Shallows", false))),
                    Note(2, "Idea", false, "A phone that asks why.", emptyList())
                )
            ),
            onBack = {},
            onAction = {}
        )
    }
}
