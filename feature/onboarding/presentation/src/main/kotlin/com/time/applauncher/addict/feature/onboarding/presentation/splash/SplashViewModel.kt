package com.time.applauncher.addict.feature.onboarding.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface SplashAction {
    data object OnTap : SplashAction
}

sealed interface SplashEvent {
    data object GoHome : SplashEvent
    data object GoOnboarding : SplashEvent
}

class SplashViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _events = Channel<SplashEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            if (settingsRepository.settings.first().onboardingComplete) {
                _events.send(SplashEvent.GoHome)
            }
        }
    }

    fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.OnTap -> viewModelScope.launch {
                val done = settingsRepository.settings.first().onboardingComplete
                _events.send(if (done) SplashEvent.GoHome else SplashEvent.GoOnboarding)
            }
        }
    }
}
