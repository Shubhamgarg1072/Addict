package com.time.applauncher.addict.feature.notes.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.feature.notes.domain.Note
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotesState(
    val notes: List<Note> = emptyList(),
    val isEditing: Boolean = false,
    val draftTitle: String = "",
    val draftBody: String = "",
    val addingItemNoteId: Long? = null,
    val draftItem: String = ""
) {
    val canSaveNote: Boolean get() = draftTitle.isNotBlank()
    val canSaveItem: Boolean get() = draftItem.isNotBlank()
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
}

class NotesViewModel(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        NotesState(
            isEditing = savedStateHandle[KEY_EDITING] ?: false,
            draftTitle = savedStateHandle[KEY_TITLE] ?: "",
            draftBody = savedStateHandle[KEY_BODY] ?: ""
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.observeNotes().collect { notes ->
                _state.update { it.copy(notes = notes) }
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

    private fun clearNoteDraft() {
        savedStateHandle[KEY_EDITING] = false
        savedStateHandle[KEY_TITLE] = ""
        savedStateHandle[KEY_BODY] = ""
        _state.update { it.copy(isEditing = false, draftTitle = "", draftBody = "") }
    }

    private companion object {
        const val KEY_EDITING = "draft_editing"
        const val KEY_TITLE = "draft_title"
        const val KEY_BODY = "draft_body"
    }
}
