package com.time.applauncher.addict.feature.agenda.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.feature.agenda.domain.AgendaDay
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val AGENDA_DAY_OPTIONS = listOf("TODAY", "TOMORROW")

data class AgendaState(
    val days: List<AgendaDay> = emptyList(),
    val isEditing: Boolean = false,
    val draftDay: String = "TODAY",
    val draftTime: String = "",
    val draftTitle: String = "",
    val draftMeta: String = ""
) {
    val canSave: Boolean get() = draftTitle.isNotBlank() && draftTime.isNotBlank()
}

sealed interface AgendaAction {
    data object OnAddEvent : AgendaAction
    data object OnDismissEditor : AgendaAction
    data class OnDraftDayChange(val day: String) : AgendaAction
    data class OnDraftTimeChange(val time: String) : AgendaAction
    data class OnDraftTitleChange(val title: String) : AgendaAction
    data class OnDraftMetaChange(val meta: String) : AgendaAction
    data object OnSaveEvent : AgendaAction
    data class OnDeleteEvent(val id: Long) : AgendaAction
}

class AgendaViewModel(
    private val agendaRepository: AgendaRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        AgendaState(
            isEditing = savedStateHandle[KEY_EDITING] ?: false,
            draftDay = savedStateHandle[KEY_DAY] ?: "TODAY",
            draftTime = savedStateHandle[KEY_TIME] ?: "",
            draftTitle = savedStateHandle[KEY_TITLE] ?: "",
            draftMeta = savedStateHandle[KEY_META] ?: ""
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            agendaRepository.observeAgenda().collect { days ->
                _state.update { it.copy(days = days) }
            }
        }
    }

    fun onAction(action: AgendaAction) {
        when (action) {
            AgendaAction.OnAddEvent -> setEditing(true)
            AgendaAction.OnDismissEditor -> clearDraft()
            is AgendaAction.OnDraftDayChange -> updateDraft { it.copy(draftDay = action.day) }
            is AgendaAction.OnDraftTimeChange -> updateDraft { it.copy(draftTime = action.time) }
            is AgendaAction.OnDraftTitleChange -> updateDraft { it.copy(draftTitle = action.title) }
            is AgendaAction.OnDraftMetaChange -> updateDraft { it.copy(draftMeta = action.meta) }
            AgendaAction.OnSaveEvent -> save()
            is AgendaAction.OnDeleteEvent -> viewModelScope.launch {
                agendaRepository.deleteEvent(action.id)
            }
        }
    }

    private fun save() {
        val draft = _state.value
        if (!draft.canSave) return
        viewModelScope.launch {
            agendaRepository.addEvent(
                day = draft.draftDay,
                time = draft.draftTime.trim(),
                title = draft.draftTitle.trim(),
                meta = draft.draftMeta.trim()
            )
            clearDraft()
        }
    }

    private fun setEditing(editing: Boolean) {
        savedStateHandle[KEY_EDITING] = editing
        _state.update { it.copy(isEditing = editing) }
    }

    private fun clearDraft() {
        savedStateHandle[KEY_EDITING] = false
        savedStateHandle[KEY_DAY] = "TODAY"
        savedStateHandle[KEY_TIME] = ""
        savedStateHandle[KEY_TITLE] = ""
        savedStateHandle[KEY_META] = ""
        _state.update {
            it.copy(isEditing = false, draftDay = "TODAY", draftTime = "", draftTitle = "", draftMeta = "")
        }
    }

    private fun updateDraft(transform: (AgendaState) -> AgendaState) {
        _state.update(transform)
        val s = _state.value
        savedStateHandle[KEY_DAY] = s.draftDay
        savedStateHandle[KEY_TIME] = s.draftTime
        savedStateHandle[KEY_TITLE] = s.draftTitle
        savedStateHandle[KEY_META] = s.draftMeta
    }

    private companion object {
        const val KEY_EDITING = "draft_editing"
        const val KEY_DAY = "draft_day"
        const val KEY_TIME = "draft_time"
        const val KEY_TITLE = "draft_title"
        const val KEY_META = "draft_meta"
    }
}
