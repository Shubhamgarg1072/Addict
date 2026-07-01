package com.time.applauncher.addict.feature.home.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class FocusActiveState(
    val remainingSeconds: Int = 0
)

sealed interface FocusActiveAction {
    data object OnEndFocus : FocusActiveAction
}

sealed interface FocusActiveEvent {
    data object GoHome : FocusActiveEvent
}

class FocusActiveViewModel(
    minutes: Int
) : ViewModel() {

    private val _state = MutableStateFlow(FocusActiveState(remainingSeconds = minutes * 60))
    val state = _state.asStateFlow()

    private val _events = Channel<FocusActiveEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            while (_state.value.remainingSeconds > 0) {
                delay(1000)
                _state.value = FocusActiveState(_state.value.remainingSeconds - 1)
            }
            _events.send(FocusActiveEvent.GoHome)
        }
    }

    fun onAction(action: FocusActiveAction) {
        when (action) {
            FocusActiveAction.OnEndFocus -> viewModelScope.launch {
                _events.send(FocusActiveEvent.GoHome)
            }
        }
    }
}
