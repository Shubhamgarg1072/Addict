package com.time.applauncher.addict.feature.notes.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.feature.notes.domain.Challenge
import com.time.applauncher.addict.feature.notes.domain.ChallengeRepository
import com.time.applauncher.addict.feature.notes.domain.Note
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class DayCellUi(
    val label: String,
    val done: Boolean,
    val isToday: Boolean
)

data class ChallengeUi(
    val id: Long,
    val title: String,
    val goalLabel: String,
    val streak: Int,
    val progress: Float,
    val doneToday: Boolean,
    val week: List<DayCellUi>,
    val reminderLabel: String,
    val hasReminder: Boolean
)

data class NotesState(
    val notes: List<Note> = emptyList(),
    val isEditing: Boolean = false,
    val draftTitle: String = "",
    val draftBody: String = "",
    val addingItemNoteId: Long? = null,
    val draftItem: String = "",
    val challenges: List<ChallengeUi> = emptyList(),
    val isSettingChallenge: Boolean = false,
    val challengeTitle: String = "",
    val challengeTarget: String = ""
) {
    val canSaveNote: Boolean get() = draftTitle.isNotBlank()
    val canSaveItem: Boolean get() = draftItem.isNotBlank()
    val canSaveChallenge: Boolean get() = challengeTitle.isNotBlank()
}

sealed interface NotesAction {
    data class OnToggleItem(val itemId: Long, val done: Boolean) : NotesAction
    data object OnAddNote : NotesAction
    data object OnDismissEditor : NotesAction
    data class OnDraftTitleChange(val title: String) : NotesAction
    data class OnDraftBodyChange(val body: String) : NotesAction
    data object OnSaveNote : NotesAction
    data class OnDeleteNote(val noteId: Long) : NotesAction
    data class OnStartAddItem(val noteId: Long) : NotesAction
    data class OnDraftItemChange(val text: String) : NotesAction
    data object OnSaveItem : NotesAction

    // Challenges
    data class OnMarkChallengeToday(val id: Long, val done: Boolean) : NotesAction
    data class OnCycleReminder(val id: Long) : NotesAction
    data class OnDeleteChallenge(val id: Long) : NotesAction
    data object OnSetChallenge : NotesAction
    data object OnDismissChallengeEditor : NotesAction
    data class OnChallengeTitleChange(val title: String) : NotesAction
    data class OnChallengeTargetChange(val target: String) : NotesAction
    data object OnSaveChallenge : NotesAction
}

class NotesViewModel(
    private val noteRepository: NoteRepository,
    private val challengeRepository: ChallengeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        NotesState(
            isEditing = savedStateHandle[KEY_EDITING] ?: false,
            draftTitle = savedStateHandle[KEY_TITLE] ?: "",
            draftBody = savedStateHandle[KEY_BODY] ?: "",
            isSettingChallenge = savedStateHandle[KEY_CH_EDITING] ?: false,
            challengeTitle = savedStateHandle[KEY_CH_TITLE] ?: "",
            challengeTarget = savedStateHandle[KEY_CH_TARGET] ?: ""
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.observeNotes().collect { notes ->
                _state.update { it.copy(notes = notes) }
            }
        }
        viewModelScope.launch {
            challengeRepository.observeChallenges().collect { challenges ->
                val todayDow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
                _state.update { it.copy(challenges = challenges.map { c -> c.toUi(todayDow) }) }
            }
        }
    }

    fun onAction(action: NotesAction) {
        when (action) {
            is NotesAction.OnToggleItem -> viewModelScope.launch {
                noteRepository.setItemDone(action.itemId, action.done)
            }
            NotesAction.OnAddNote -> {
                savedStateHandle[KEY_EDITING] = true
                _state.update { it.copy(isEditing = true, addingItemNoteId = null, draftItem = "") }
            }
            NotesAction.OnDismissEditor -> clearNoteDraft()
            is NotesAction.OnDraftTitleChange -> {
                savedStateHandle[KEY_TITLE] = action.title
                _state.update { it.copy(draftTitle = action.title) }
            }
            is NotesAction.OnDraftBodyChange -> {
                savedStateHandle[KEY_BODY] = action.body
                _state.update { it.copy(draftBody = action.body) }
            }
            NotesAction.OnSaveNote -> saveNote()
            is NotesAction.OnDeleteNote -> viewModelScope.launch {
                noteRepository.deleteNote(action.noteId)
            }
            is NotesAction.OnStartAddItem ->
                _state.update { it.copy(addingItemNoteId = action.noteId, draftItem = "", isEditing = false) }
            is NotesAction.OnDraftItemChange -> _state.update { it.copy(draftItem = action.text) }
            NotesAction.OnSaveItem -> saveItem()

            is NotesAction.OnMarkChallengeToday -> viewModelScope.launch {
                challengeRepository.setDoneToday(action.id, action.done)
            }
            is NotesAction.OnCycleReminder -> viewModelScope.launch {
                challengeRepository.cycleReminder(action.id)
            }
            is NotesAction.OnDeleteChallenge -> viewModelScope.launch {
                challengeRepository.deleteChallenge(action.id)
            }
            NotesAction.OnSetChallenge -> {
                savedStateHandle[KEY_CH_EDITING] = true
                _state.update { it.copy(isSettingChallenge = true) }
            }
            NotesAction.OnDismissChallengeEditor -> clearChallengeDraft()
            is NotesAction.OnChallengeTitleChange -> {
                savedStateHandle[KEY_CH_TITLE] = action.title
                _state.update { it.copy(challengeTitle = action.title) }
            }
            is NotesAction.OnChallengeTargetChange -> {
                val digits = action.target.filter(Char::isDigit).take(3)
                savedStateHandle[KEY_CH_TARGET] = digits
                _state.update { it.copy(challengeTarget = digits) }
            }
            NotesAction.OnSaveChallenge -> saveChallenge()
        }
    }

    private fun saveNote() {
        val draft = _state.value
        if (!draft.canSaveNote) return
        viewModelScope.launch {
            noteRepository.addNote(
                title = draft.draftTitle.trim(),
                body = draft.draftBody.trim().ifEmpty { null }
            )
            clearNoteDraft()
        }
    }

    private fun saveItem() {
        val draft = _state.value
        val noteId = draft.addingItemNoteId ?: return
        if (!draft.canSaveItem) return
        viewModelScope.launch {
            noteRepository.addItem(noteId, draft.draftItem.trim())
            _state.update { it.copy(addingItemNoteId = null, draftItem = "") }
        }
    }

    private fun saveChallenge() {
        val draft = _state.value
        if (!draft.canSaveChallenge) return
        val title = draft.challengeTitle.trim()
        viewModelScope.launch {
            challengeRepository.addChallenge(
                title = title,
                shortLabel = title.substringBefore(' '),
                targetDays = draft.challengeTarget.toIntOrNull()?.coerceIn(1, 365) ?: DEFAULT_TARGET
            )
            clearChallengeDraft()
        }
    }

    private fun clearNoteDraft() {
        savedStateHandle[KEY_EDITING] = false
        savedStateHandle[KEY_TITLE] = ""
        savedStateHandle[KEY_BODY] = ""
        _state.update { it.copy(isEditing = false, draftTitle = "", draftBody = "") }
    }

    private fun clearChallengeDraft() {
        savedStateHandle[KEY_CH_EDITING] = false
        savedStateHandle[KEY_CH_TITLE] = ""
        savedStateHandle[KEY_CH_TARGET] = ""
        _state.update { it.copy(isSettingChallenge = false, challengeTitle = "", challengeTarget = "") }
    }

    private fun Challenge.toUi(todayDow: Int): ChallengeUi = ChallengeUi(
        id = id,
        title = title,
        goalLabel = "GOAL · $targetDays DAYS",
        streak = streak,
        progress = progress,
        doneToday = doneToday,
        week = week.mapIndexed { i, done ->
            val dow = ((todayDow - (Challenge.LAST_DAY - i)) % 7 + 7) % 7
            DayCellUi(label = DAY_LETTERS[dow], done = done, isToday = i == Challenge.LAST_DAY)
        },
        reminderLabel = reminder?.let { "Remind $it" } ?: "No reminder",
        hasReminder = reminder != null
    )

    private companion object {
        const val KEY_EDITING = "draft_editing"
        const val KEY_TITLE = "draft_title"
        const val KEY_BODY = "draft_body"
        const val KEY_CH_EDITING = "ch_editing"
        const val KEY_CH_TITLE = "ch_title"
        const val KEY_CH_TARGET = "ch_target"
        const val DEFAULT_TARGET = 30

        val DAY_LETTERS = listOf("S", "M", "T", "W", "T", "F", "S")
    }
}
