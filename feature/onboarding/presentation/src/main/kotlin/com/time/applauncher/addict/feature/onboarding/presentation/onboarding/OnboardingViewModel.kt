package com.time.applauncher.addict.feature.onboarding.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface OnboardingAction {
    data object OnNext : OnboardingAction
}

sealed interface OnboardingEvent {
    data object GoPermission : OnboardingEvent
}

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(0)
    val state = _state.asStateFlow()

    private val _events = Channel<OnboardingEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.OnNext -> {
                if (_state.value < LAST_STEP) {
                    _state.update { it + 1 }
                } else {
                    viewModelScope.launch { _events.send(OnboardingEvent.GoPermission) }
                }
            }
        }
    }

    private companion object {
        const val LAST_STEP = 2
    }
}
