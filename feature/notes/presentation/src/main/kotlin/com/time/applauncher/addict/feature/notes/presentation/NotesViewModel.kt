package com.time.applauncher.addict.feature.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.feature.notes.domain.Note
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotesState(
    val notes: List<Note> = emptyList()
)

sealed interface NotesAction {
    data class OnToggleItem(val itemId: Long, val done: Boolean) : NotesAction
    data object OnAddNote : NotesAction
}

class NotesViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.ensureSeeded()
        }
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
            NotesAction.OnAddNote -> viewModelScope.launch {
                noteRepository.addNote("New note")
            }
        }
    }
}
