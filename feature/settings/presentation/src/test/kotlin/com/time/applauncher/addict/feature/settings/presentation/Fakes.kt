package com.time.applauncher.addict.feature.settings.presentation

import com.time.applauncher.addict.core.domain.model.AppInfo
import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.model.UserSettings
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.util.DataError
import com.time.applauncher.addict.core.domain.util.EmptyResult
import com.time.applauncher.addict.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsRepository(
    initial: UserSettings = UserSettings()
) : SettingsRepository {
    val flow = MutableStateFlow(initial)
    override val settings = flow

    override suspend fun setTheme(theme: ThemeMode) = update { it.copy(theme = theme) }
    override suspend fun setUse24h(value: Boolean) = update { it.copy(use24h = value) }
    override suspend fun setShowQuote(value: Boolean) = update { it.copy(showQuote = value) }
    override suspend fun setColorfulIcons(value: Boolean) = update { it.copy(colorfulIcons = value) }
    override suspend fun setHighContrast(value: Boolean) = update { it.copy(highContrast = value) }
    override suspend fun setGoalMinutes(minutes: Int) = update { it.copy(goalMinutes = minutes) }
    override suspend fun setLaunchDelaySeconds(seconds: Int) =
        update { it.copy(launchDelaySeconds = seconds) }
    override suspend fun setOnboardingComplete(value: Boolean) =
        update { it.copy(onboardingComplete = value) }
    override suspend fun setFavorites(favorites: List<String>) =
        update { it.copy(favorites = favorites) }
    override suspend fun toggleDistracting(packageName: String) = update {
        val current = it.distractingPackages
        it.copy(
            distractingPackages =
                if (packageName in current) current - packageName else current + packageName
        )
    }

    private fun update(transform: (UserSettings) -> UserSettings) {
        flow.update(transform)
    }
}

class FakeAppRepository(
    apps: List<AppInfo>,
    private val settingsRepository: FakeSettingsRepository
) : AppRepository {
    private val installed = MutableStateFlow(apps)

    // Mirrors LauncherAppsRepository: isDistracting is derived from the settings set.
    override fun observeApps(): Flow<List<AppInfo>> =
        kotlinx.coroutines.flow.combine(installed, settingsRepository.flow) { apps, settings ->
            apps.map { it.copy(isDistracting = it.packageName in settings.distractingPackages) }
        }

    override fun launch(packageName: String): EmptyResult<DataError.Local> = Result.Success(Unit)
}
