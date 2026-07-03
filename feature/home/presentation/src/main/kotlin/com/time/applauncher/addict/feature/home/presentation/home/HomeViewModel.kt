package com.time.applauncher.addict.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.model.DailyUsage
import com.time.applauncher.addict.core.domain.model.UserSettings
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import com.time.applauncher.addict.core.domain.util.getOrNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

data class FavoriteUi(
    val packageName: String,
    val label: String,
    val isDistracting: Boolean
)

data class HomeState(
    val clock: String = "",
    val date: String = "",
    val screenTime: String = "0h 0m",
    val unlocks: Int = 0,
    val goalFraction: Float = 0f,
    val goalOver: Boolean = false,
    val goalRemaining: String = "",
    val favorites: List<FavoriteUi> = emptyList(),
    val showQuote: Boolean = true,
    val quote: String = "“The best app is the one you never open.”"
)

sealed interface HomeAction {
    data object OnClickStats : HomeAction
    data object OnClickDate : HomeAction
    data object OnClickGoal : HomeAction
    data object OnClickSearch : HomeAction
    data class OnClickFavorite(val favorite: FavoriteUi) : HomeAction
}

sealed interface HomeEvent {
    data object NavigateToDashboard : HomeEvent
    data object NavigateToAgenda : HomeEvent
    data object NavigateToGoal : HomeEvent
    data object NavigateToSearch : HomeEvent
    data class NavigateToGate(val packageName: String, val label: String) : HomeEvent
}

class HomeViewModel(
    private val appRepository: AppRepository,
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    private val usageFlow = MutableStateFlow<DailyUsage?>(null)

    private val ticker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }

    init {
        // Usage stats can start flowing after init (e.g. permission granted later),
        // so poll instead of fetching once.
        viewModelScope.launch {
            while (true) {
                usageFlow.value = usageRepository.getToday().getOrNull()
                delay(30_000)
            }
        }
        combine(
            ticker,
            settingsRepository.settings,
            appRepository.observeApps(),
            usageFlow
        ) { _, settings, apps, usage ->
            buildState(settings, apps, usage)
        }
            .onEach { newState -> _state.value = newState }
            .launchIn(viewModelScope)
    }

    private fun buildState(
        settings: UserSettings,
        apps: List<com.time.applauncher.addict.core.domain.model.AppInfo>,
        usage: DailyUsage?
    ): HomeState {
        val now = Calendar.getInstance()

        val favorites = resolveFavorites(settings, apps)

        val usedMinutes = ((usage?.totalMs ?: 0L) / 60_000).toInt()
        val limit = settings.goalMinutes
        val over = usedMinutes > limit
        val remaining = (limit - usedMinutes).coerceAtLeast(0)

        return HomeState(
            clock = formatClock(now, settings.use24h),
            date = formatDate(now),
            screenTime = formatHm((usage?.totalMs ?: 0L)),
            unlocks = usage?.unlocks ?: 0,
            goalFraction = (usedMinutes.toFloat() / limit).coerceIn(0f, 1f),
            goalOver = over,
            goalRemaining = if (over) "${usedMinutes - limit}m over" else "${remaining / 60}h ${remaining % 60}m left",
            favorites = favorites,
            showQuote = settings.showQuote
        )
    }

    private fun resolveFavorites(
        settings: UserSettings,
        apps: List<com.time.applauncher.addict.core.domain.model.AppInfo>
    ): List<FavoriteUi> {
        val byPackage = apps.associateBy { it.packageName }
        val chosen = settings.favorites.mapNotNull { byPackage[it] }
        val resolved = chosen.ifEmpty { apps.filter { !it.isDistracting }.take(6) }
        return resolved.map { FavoriteUi(it.packageName, it.label, it.isDistracting) }
    }

    fun onAction(action: HomeAction) {
        viewModelScope.launch {
            when (action) {
                HomeAction.OnClickStats -> _events.send(HomeEvent.NavigateToDashboard)
                HomeAction.OnClickDate -> _events.send(HomeEvent.NavigateToAgenda)
                HomeAction.OnClickGoal -> _events.send(HomeEvent.NavigateToGoal)
                HomeAction.OnClickSearch -> _events.send(HomeEvent.NavigateToSearch)
                is HomeAction.OnClickFavorite -> {
                    val fav = action.favorite
                    if (fav.isDistracting) {
                        _events.send(HomeEvent.NavigateToGate(fav.packageName, fav.label))
                    } else {
                        appRepository.launch(fav.packageName)
                    }
                }
            }
        }
    }

    private companion object {
        val DAYS = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val MONTHS = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }

    private fun formatClock(cal: Calendar, use24h: Boolean): String {
        val minute = cal.get(Calendar.MINUTE)
        val mm = if (minute < 10) "0$minute" else "$minute"
        return if (use24h) {
            val h = cal.get(Calendar.HOUR_OF_DAY)
            val hh = if (h < 10) "0$h" else "$h"
            "$hh:$mm"
        } else {
            var h = cal.get(Calendar.HOUR_OF_DAY) % 12
            if (h == 0) h = 12
            "$h:$mm"
        }
    }

    private fun formatDate(cal: Calendar): String {
        val day = DAYS[cal.get(Calendar.DAY_OF_WEEK) - 1]
        val month = MONTHS[cal.get(Calendar.MONTH)]
        return "$day, $month ${cal.get(Calendar.DAY_OF_MONTH)}"
    }

    private fun formatHm(ms: Long): String {
        val total = ms / 60_000
        return "${total / 60}h ${total % 60}m"
    }
}
