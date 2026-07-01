package com.time.applauncher.addict.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.model.UserSettings
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val settings: UserSettings = UserSettings()
)

sealed interface SettingsAction {
    data object ToggleColorfulIcons : SettingsAction
    data object Toggle24h : SettingsAction
    data object ToggleQuote : SettingsAction
    data object ToggleHighContrast : SettingsAction
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
    }

    fun onAction(action: SettingsAction) {
        viewModelScope.launch {
            val current = _state.value.settings
            when (action) {
                SettingsAction.ToggleColorfulIcons ->
                    settingsRepository.setColorfulIcons(!current.colorfulIcons)
                SettingsAction.Toggle24h ->
                    settingsRepository.setUse24h(!current.use24h)
                SettingsAction.ToggleQuote ->
                    settingsRepository.setShowQuote(!current.showQuote)
                SettingsAction.ToggleHighContrast ->
                    settingsRepository.setHighContrast(!current.highContrast)
            }
        }
    }
}
