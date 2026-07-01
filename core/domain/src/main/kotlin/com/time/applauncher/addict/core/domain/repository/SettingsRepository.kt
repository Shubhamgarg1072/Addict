package com.time.applauncher.addict.core.domain.repository

import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<UserSettings>

    suspend fun setTheme(theme: ThemeMode)
    suspend fun setUse24h(value: Boolean)
    suspend fun setShowQuote(value: Boolean)
    suspend fun setColorfulIcons(value: Boolean)
    suspend fun setHighContrast(value: Boolean)
    suspend fun setGoalMinutes(minutes: Int)
    suspend fun setLaunchDelaySeconds(seconds: Int)
    suspend fun setOnboardingComplete(value: Boolean)
    suspend fun setFavorites(favorites: List<String>)
    suspend fun toggleDistracting(packageName: String)
}
