package com.time.applauncher.addict.core.data.usage

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import com.time.applauncher.addict.core.domain.model.AppUsage
import com.time.applauncher.addict.core.domain.model.DailyUsage
import com.time.applauncher.addict.core.domain.model.DayUsage
import com.time.applauncher.addict.core.domain.model.WeekUsage
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import com.time.applauncher.addict.core.domain.util.DataError
import com.time.applauncher.addict.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class UsageStatsRepository(
    private val context: Context
) : UsageRepository {

    private val usageStatsManager: UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val labelCache = mutableMapOf<String, String>()

    override fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override suspend fun getToday(): Result<DailyUsage, DataError.Local> =
        withContext(Dispatchers.IO) {
            if (!hasUsageAccess()) return@withContext Result.Error(DataError.Local.PERMISSION_DENIED)
            try {
                val start = startOfToday()
                val now = System.currentTimeMillis()
                val stats = usageStatsManager.queryAndAggregateUsageStats(start, now)
                val self = context.packageName

                val perApp = stats.values
                    .filter { it.totalTimeInForeground > 0 && it.packageName != self }
                    .sortedByDescending { it.totalTimeInForeground }
                    .map { AppUsage(it.packageName, labelOf(it.packageName), it.totalTimeInForeground) }

                val total = perApp.sumOf { it.foregroundMs }
                Result.Success(
                    DailyUsage(totalMs = total, unlocks = countUnlocks(start, now), perApp = perApp)
                )
            } catch (e: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }

    override suspend fun getWeek(): Result<WeekUsage, DataError.Local> = weekEndingDaysAgo(0)

    override suspend fun getPreviousWeek(): Result<WeekUsage, DataError.Local> = weekEndingDaysAgo(7)

    private suspend fun weekEndingDaysAgo(endOffset: Int): Result<WeekUsage, DataError.Local> =
        withContext(Dispatchers.IO) {
            if (!hasUsageAccess()) return@withContext Result.Error(DataError.Local.PERMISSION_DENIED)
            try {
                val days = ArrayList<DayUsage>(7)
                var sum = 0L
                for (offset in (endOffset + 6) downTo endOffset) {
                    val cal = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -offset)
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }
                    val dayStart = cal.timeInMillis
                    val dayEnd = dayStart + DAY_MS
                    val label = DAY_LETTERS[cal.get(Calendar.DAY_OF_WEEK) - 1]
                    val stats = usageStatsManager.queryAndAggregateUsageStats(
                        dayStart, minOf(dayEnd, System.currentTimeMillis())
                    )
                    val total = stats.values.sumOf { it.totalTimeInForeground }
                    sum += total
                    days += DayUsage(label, total)
                }
                Result.Success(WeekUsage(days = days, dailyAverageMs = sum / 7))
            } catch (e: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }

    private fun countUnlocks(start: Long, end: Long): Int {
        val events = usageStatsManager.queryEvents(start, end)
        val event = UsageEvents.Event()
        var count = 0
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.KEYGUARD_HIDDEN) count++
        }
        return count
    }

    private fun labelOf(pkg: String): String = labelCache.getOrPut(pkg) {
        try {
            val pm = context.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
        } catch (e: Exception) {
            pkg.substringAfterLast('.')
        }
    }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private companion object {
        const val DAY_MS = 24L * 60 * 60 * 1000
        // Calendar.DAY_OF_WEEK is 1=Sunday..7=Saturday
        val DAY_LETTERS = listOf("S", "M", "T", "W", "T", "F", "S")
    }
}
