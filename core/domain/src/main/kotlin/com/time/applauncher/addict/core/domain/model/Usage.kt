package com.time.applauncher.addict.core.domain.model

data class AppUsage(
    val packageName: String,
    val label: String,
    val foregroundMs: Long
)

data class DailyUsage(
    val totalMs: Long,
    val unlocks: Int,
    val perApp: List<AppUsage>
)

data class DayUsage(
    val label: String,
    val totalMs: Long
)

data class WeekUsage(
    val days: List<DayUsage>,
    val dailyAverageMs: Long
)
