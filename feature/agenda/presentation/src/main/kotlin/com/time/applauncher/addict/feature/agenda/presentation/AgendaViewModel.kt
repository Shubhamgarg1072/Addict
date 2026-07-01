package com.time.applauncher.addict.feature.agenda.presentation

import androidx.lifecycle.ViewModel
import com.time.applauncher.addict.feature.agenda.domain.AgendaDay
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgendaViewModel(
    agendaRepository: AgendaRepository
) : ViewModel() {
    private val _state = MutableStateFlow<List<AgendaDay>>(agendaRepository.getAgenda())
    val state = _state.asStateFlow()
}
