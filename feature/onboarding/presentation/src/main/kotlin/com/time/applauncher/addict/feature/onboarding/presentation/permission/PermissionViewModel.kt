package com.time.applauncher.addict.feature.onboarding.presentation.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface PermissionAction {
    data object OnComplete : PermissionAction
}

sealed interface PermissionEvent {
    data object Done : PermissionEvent
}

class PermissionViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _events = Channel<PermissionEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: PermissionAction) {
        when (action) {
            PermissionAction.OnComplete -> viewModelScope.launch {
                settingsRepository.setOnboardingComplete(true)
                _events.send(PermissionEvent.Done)
            }
        }
    }
}
