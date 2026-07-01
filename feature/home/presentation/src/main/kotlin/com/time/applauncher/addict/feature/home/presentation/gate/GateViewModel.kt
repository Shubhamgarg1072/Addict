package com.time.applauncher.addict.feature.home.presentation.gate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import com.time.applauncher.addict.core.domain.util.getOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GateState(
    val appLabel: String = "",
    val spent: String = "0h 0m",
    val selectedReason: Int? = null,
    val countdown: Int? = null
)

sealed interface GateAction {
    data class OnSelectReason(val index: Int) : GateAction
    data object OnNotNow : GateAction
    data object OnOpenAnyway : GateAction
    data object OnCancel : GateAction
}

sealed interface GateEvent {
    data object GoHome : GateEvent
}

val GATE_REASONS = listOf("Reply to someone", "Post something", "Watch for 5 minutes", "Just browsing")

class GateViewModel(
    private val packageName: String,
    appLabel: String,
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GateState(appLabel = appLabel))
    val state = _state.asStateFlow()

    private val _events = Channel<GateEvent>()
    val events = _events.receiveAsFlow()

    private var countdownJob: Job? = null

    init {
        viewModelScope.launch {
            val ms = usageRepository.getToday().getOrNull()?.totalMs ?: 0L
            val total = ms / 60_000
            _state.update { it.copy(spent = "${total / 60}h ${total % 60}m") }
        }
    }

    fun onAction(action: GateAction) {
        when (action) {
            is GateAction.OnSelectReason -> _state.update { it.copy(selectedReason = action.index) }
            GateAction.OnNotNow -> emitHome()
            GateAction.OnCancel -> {
                countdownJob?.cancel()
                _state.update { it.copy(countdown = null) }
            }
            GateAction.OnOpenAnyway -> startCountdown()
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            val seconds = settingsRepository.settings.first().launchDelaySeconds.coerceAtLeast(1)
            for (remaining in seconds downTo 1) {
                _state.update { it.copy(countdown = remaining) }
                delay(1000)
            }
            appRepository.launch(packageName)
            _state.update { it.copy(countdown = null) }
            _events.send(GateEvent.GoHome)
        }
    }

    private fun emitHome() {
        viewModelScope.launch { _events.send(GateEvent.GoHome) }
    }
}
