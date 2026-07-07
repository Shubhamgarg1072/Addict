package com.time.applauncher.addict.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

const val MAX_FAVORITES = 8

data class FavoriteAppUi(
    val packageName: String,
    val label: String,
    val selected: Boolean
)

data class FavoritesState(
    val apps: List<FavoriteAppUi> = emptyList(),
    val selectedCount: Int = 0
) {
    val atLimit: Boolean get() = selectedCount >= MAX_FAVORITES
}

sealed interface FavoritesAction {
    data class OnToggleApp(val packageName: String) : FavoritesAction
}

class FavoritesViewModel(
    private val appRepository: AppRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    init {
        combine(appRepository.observeApps(), settingsRepository.settings) { apps, settings ->
            val favorites = settings.favorites.toSet()
            FavoritesState(
                apps = apps.map { FavoriteAppUi(it.packageName, it.label, it.packageName in favorites) },
                selectedCount = apps.count { it.packageName in favorites }
            )
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun onAction(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.OnToggleApp -> viewModelScope.launch {
                val current = settingsRepository.settings.first().favorites
                val next = when {
                    action.packageName in current -> current - action.packageName
                    _state.value.atLimit -> return@launch
                    else -> current + action.packageName
                }
                settingsRepository.setFavorites(next)
            }
        }
    }
}
