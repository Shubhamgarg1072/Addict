package com.time.applauncher.addict.feature.settings.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.model.UserSettings
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.feature.settings.presentation.backup.BackupService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val LAUNCH_DELAY_OPTIONS = listOf(3, 5, 10, 15, 30)

data class SettingsState(
    val settings: UserSettings = UserSettings(),
    val dataMessage: String? = null
)

sealed interface SettingsAction {
    data object CycleTheme : SettingsAction
    data object ToggleColorfulIcons : SettingsAction
    data object Toggle24h : SettingsAction
    data object ToggleQuote : SettingsAction
    data object ToggleHighContrast : SettingsAction
    data object CycleLaunchDelay : SettingsAction
    data class OnBackupTarget(val uri: Uri) : SettingsAction
    data class OnRestoreSource(val uri: Uri) : SettingsAction
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val backupService: BackupService
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private var messageJob: Job? = null

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
                SettingsAction.CycleTheme -> {
                    val modes = ThemeMode.entries
                    settingsRepository.setTheme(modes[(current.theme.ordinal + 1) % modes.size])
                }
                SettingsAction.ToggleColorfulIcons ->
                    settingsRepository.setColorfulIcons(!current.colorfulIcons)
                SettingsAction.Toggle24h ->
                    settingsRepository.setUse24h(!current.use24h)
                SettingsAction.ToggleQuote ->
                    settingsRepository.setShowQuote(!current.showQuote)
                SettingsAction.ToggleHighContrast ->
                    settingsRepository.setHighContrast(!current.highContrast)
                SettingsAction.CycleLaunchDelay -> {
                    val index = LAUNCH_DELAY_OPTIONS.indexOf(current.launchDelaySeconds)
                    val next = LAUNCH_DELAY_OPTIONS[(index + 1) % LAUNCH_DELAY_OPTIONS.size]
                    settingsRepository.setLaunchDelaySeconds(next)
                }
                is SettingsAction.OnBackupTarget -> {
                    val ok = backupService.backupTo(action.uri)
                    showMessage(if (ok) "BACKUP SAVED" else "BACKUP FAILED")
                }
                is SettingsAction.OnRestoreSource -> {
                    val ok = backupService.restoreFrom(action.uri)
                    showMessage(if (ok) "RESTORE COMPLETE" else "RESTORE FAILED")
                }
            }
        }
    }

    private fun showMessage(text: String) {
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            _state.update { it.copy(dataMessage = text) }
            delay(3_000)
            _state.update { it.copy(dataMessage = null) }
        }
    }
}
