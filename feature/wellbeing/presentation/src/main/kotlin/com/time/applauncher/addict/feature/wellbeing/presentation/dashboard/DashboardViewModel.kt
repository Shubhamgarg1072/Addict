package com.time.applauncher.addict.feature.wellbeing.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import com.time.applauncher.addict.core.domain.util.getOrNull
import com.time.applauncher.addict.feature.wellbeing.presentation.formatHm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeekBarUi(val label: String, val fraction: Float, val isToday: Boolean)
data class TopAppUi(val name: String, val time: String, val fraction: Float)

data class DashboardState(
    val screenTime: String = "0m",
    val delta: String = "",
    val bars: List<WeekBarUi> = emptyList(),
    val dailyAvg: String = "0m",
    val streak: String = "4 days",
    val topApps: List<TopAppUi> = emptyList()
)

class DashboardViewModel(
    private val usageRepository: UsageRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val today = usageRepository.getToday().getOrNull()
            val week = usageRepository.getWeek().getOrNull()

            val maxDay = week?.days?.maxOfOrNull { it.totalMs }?.takeIf { it > 0 } ?: 1L
            val bars = week?.days?.mapIndexed { index, day ->
                WeekBarUi(
                    label = day.label,
                    fraction = (day.totalMs.toFloat() / maxDay).coerceIn(0f, 1f),
                    isToday = index == week.days.lastIndex
                )
            } ?: emptyList()

            val maxApp = today?.perApp?.maxOfOrNull { it.foregroundMs }?.takeIf { it > 0 } ?: 1L
            val topApps = today?.perApp?.take(5)?.map {
                TopAppUi(
                    name = it.label,
                    time = formatHm(it.foregroundMs),
                    fraction = (it.foregroundMs.toFloat() / maxApp).coerceIn(0f, 1f)
                )
            } ?: emptyList()

            _state.update {
                it.copy(
                    screenTime = formatHm(today?.totalMs ?: 0L),
                    delta = "↓ 22% vs last week",
                    bars = bars,
                    dailyAvg = formatHm(week?.dailyAverageMs ?: 0L),
                    topApps = topApps
                )
            }
        }
    }
}
