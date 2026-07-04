package com.time.applauncher.addict.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class DistractingAppUi(
    val packageName: String,
    val label: String,
    val gated: Boolean
)

data class DistractingAppsState(
    val apps: List<DistractingAppUi> = emptyList()
) {
    val gatedCount: Int get() = apps.count { it.gated }
}

sealed interface DistractingAppsAction {
    data class OnToggleApp(val packageName: String) : DistractingAppsAction
}

class DistractingAppsViewModel(
    appRepository: AppRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DistractingAppsState())
    val state = _state.asStateFlow()

    init {
        // AppInfo.isDistracting already reflects the persisted set.
        appRepository.observeApps()
            .map { apps ->
                DistractingAppsState(
                    apps = apps.map { DistractingAppUi(it.packageName, it.label, it.isDistracting) }
                )
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun onAction(action: DistractingAppsAction) {
        when (action) {
            is DistractingAppsAction.OnToggleApp -> viewModelScope.launch {
                settingsRepository.toggleDistracting(action.packageName)
            }
        }
    }
}
