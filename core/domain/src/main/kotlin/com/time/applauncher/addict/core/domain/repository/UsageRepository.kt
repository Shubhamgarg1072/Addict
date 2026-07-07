package com.time.applauncher.addict.core.domain.repository

import com.time.applauncher.addict.core.domain.model.DailyUsage
import com.time.applauncher.addict.core.domain.model.WeekUsage
import com.time.applauncher.addict.core.domain.util.DataError
import com.time.applauncher.addict.core.domain.util.Result

interface UsageRepository {
    fun hasUsageAccess(): Boolean
    suspend fun getToday(): Result<DailyUsage, DataError.Local>
    suspend fun getWeek(): Result<WeekUsage, DataError.Local>
    suspend fun getPreviousWeek(): Result<WeekUsage, DataError.Local>
}
