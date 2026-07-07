package com.time.applauncher.addict.feature.wellbeing.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.time.applauncher.addict.core.domain.model.WeekUsage
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import com.time.applauncher.addict.core.domain.util.getOrNull
import com.time.applauncher.addict.feature.wellbeing.presentation.formatHm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

data class WeekBarUi(val label: String, val fraction: Float, val isToday: Boolean)
data class TopAppUi(val name: String, val time: String, val fraction: Float)

data class DashboardState(
    val screenTime: String = "0m",
    val delta: String = "",
    val bars: List<WeekBarUi> = emptyList(),
    val dailyAvg: String = "0m",
    val streak: String = "—",
    val topApps: List<TopAppUi> = emptyList()
)

class DashboardViewModel(
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository
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
            val previousWeek = usageRepository.getPreviousWeek().getOrNull()
            val goalMinutes = settingsRepository.settings.first().goalMinutes

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
                    delta = weekDelta(week, previousWeek),
                    bars = bars,
                    dailyAvg = formatHm(week?.dailyAverageMs ?: 0L),
                    streak = focusStreak(week, goalMinutes),
                    topApps = topApps
                )
            }
        }
    }

    /** Percent change of this week's daily average vs the previous week's. */
    private fun weekDelta(week: WeekUsage?, previousWeek: WeekUsage?): String {
        val current = week?.dailyAverageMs ?: return ""
        val previous = previousWeek?.dailyAverageMs?.takeIf { it > 0 } ?: return ""
        val pct = ((current - previous) * 100.0 / previous).roundToInt()
        return when {
            pct < 0 -> "↓ ${abs(pct)}% vs last week"
            pct > 0 -> "↑ $pct% vs last week"
            else -> "= same as last week"
        }
    }

    /** Consecutive days (ending today) with screen time at or under the daily goal. */
    private fun focusStreak(week: WeekUsage?, goalMinutes: Int): String {
        val days = week?.days ?: return "—"
        val goalMs = goalMinutes * 60_000L
        val streak = days.reversed().takeWhile { it.totalMs <= goalMs }.count()
        return if (streak == 1) "1 day" else "$streak days"
    }
}
