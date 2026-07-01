package com.time.applauncher.addict.feature.wellbeing.presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import com.time.applauncher.addict.core.domain.util.getOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoalState(
    val usedMinutes: Int = 0,
    val limitMinutes: Int = 150
)

sealed interface GoalAction {
    data class OnSelectLimit(val minutes: Int) : GoalAction
}

class GoalViewModel(
    private val settingsRepository: SettingsRepository,
    private val usageRepository: UsageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GoalState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val usedMs = usageRepository.getToday().getOrNull()?.totalMs ?: 0L
            _state.update { it.copy(usedMinutes = (usedMs / 60_000).toInt()) }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update { it.copy(limitMinutes = settings.goalMinutes) }
            }
        }
    }

    fun onAction(action: GoalAction) {
        when (action) {
            is GoalAction.OnSelectLimit -> viewModelScope.launch {
                settingsRepository.setGoalMinutes(action.minutes)
            }
        }
    }
}
