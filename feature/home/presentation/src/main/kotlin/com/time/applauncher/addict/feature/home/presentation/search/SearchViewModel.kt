package com.time.applauncher.addict.feature.home.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.model.AppInfo
import com.time.applauncher.addict.core.domain.repository.AppRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

enum class BuiltinTarget { NOTES, AGENDA, DASHBOARD, SETTINGS }

data class BuiltinItemUi(val name: String, val target: BuiltinTarget)
data class RecentItemUi(val packageName: String, val name: String, val isDistracting: Boolean)
data class ResultItemUi(
    val packageName: String,
    val name: String,
    val isDistracting: Boolean,
    val showLetter: Boolean,
    val letter: String
)

data class SearchState(
    val query: String = "",
    val showRecents: Boolean = true,
    val recents: List<RecentItemUi> = emptyList(),
    val builtins: List<BuiltinItemUi> = emptyList(),
    val results: List<ResultItemUi> = emptyList()
)

sealed interface SearchAction {
    data class OnKey(val char: Char) : SearchAction
    data object OnSpace : SearchAction
    data object OnBackspace : SearchAction
    data class OnSelectApp(val packageName: String, val label: String, val isDistracting: Boolean) : SearchAction
    data class OnSelectBuiltin(val target: BuiltinTarget) : SearchAction
    data object OnClose : SearchAction
}

sealed interface SearchEvent {
    data class NavigateToGate(val packageName: String, val label: String) : SearchEvent
    data object NavigateToNotes : SearchEvent
    data object NavigateToAgenda : SearchEvent
    data object NavigateToDashboard : SearchEvent
    data object NavigateToSettings : SearchEvent
    data object Close : SearchEvent
}

private val ALL_BUILTINS = listOf(
    BuiltinItemUi("Notes", BuiltinTarget.NOTES),
    BuiltinItemUi("Agenda", BuiltinTarget.AGENDA),
    BuiltinItemUi("Screen time", BuiltinTarget.DASHBOARD),
    BuiltinItemUi("Settings", BuiltinTarget.SETTINGS)
)

class SearchViewModel(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _events = Channel<SearchEvent>()
    val events = _events.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        combine(appRepository.observeApps(), queryFlow) { apps, query ->
            buildState(apps, query)
        }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    private fun buildState(apps: List<AppInfo>, query: String): SearchState {
        val q = query.trim().lowercase()
        val recents = apps.take(4).map { RecentItemUi(it.packageName, it.label, it.isDistracting) }

        val filtered = apps.filter { it.label.lowercase().contains(q) }
        var lastLetter = ""
        val results = filtered.map { app ->
            val letter = app.label.first().uppercaseChar().toString()
            val showLetter = q.isEmpty() && letter != lastLetter
            if (showLetter) lastLetter = letter
            ResultItemUi(app.packageName, app.label, app.isDistracting, showLetter, letter)
        }

        val builtins = ALL_BUILTINS.filter { it.name.lowercase().contains(q) }

        return SearchState(
            query = query,
            showRecents = q.isEmpty(),
            recents = recents,
            builtins = builtins,
            results = results
        )
    }

    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnKey -> queryFlow.value += action.char
            SearchAction.OnSpace -> queryFlow.value += " "
            SearchAction.OnBackspace -> queryFlow.value = queryFlow.value.dropLast(1)
            SearchAction.OnClose -> send(SearchEvent.Close)
            is SearchAction.OnSelectApp -> {
                if (action.isDistracting) {
                    send(SearchEvent.NavigateToGate(action.packageName, action.label))
                } else {
                    appRepository.launch(action.packageName)
                    send(SearchEvent.Close)
                }
            }
            is SearchAction.OnSelectBuiltin -> when (action.target) {
                BuiltinTarget.NOTES -> send(SearchEvent.NavigateToNotes)
                BuiltinTarget.AGENDA -> send(SearchEvent.NavigateToAgenda)
                BuiltinTarget.DASHBOARD -> send(SearchEvent.NavigateToDashboard)
                BuiltinTarget.SETTINGS -> send(SearchEvent.NavigateToSettings)
            }
        }
    }

    private fun send(event: SearchEvent) {
        viewModelScope.launch { _events.send(event) }
    }
}
