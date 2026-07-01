package com.time.applauncher.addict.feature.wellbeing.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.time.applauncher.addict.core.domain.model.DailyUsage
import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.model.UserSettings
import com.time.applauncher.addict.core.domain.model.WeekUsage
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import com.time.applauncher.addict.core.domain.util.DataError
import com.time.applauncher.addict.core.domain.util.Result
import com.time.applauncher.addict.feature.wellbeing.presentation.goal.GoalAction
import com.time.applauncher.addict.feature.wellbeing.presentation.goal.GoalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private class FakeSettingsRepository(
    initial: UserSettings = UserSettings()
) : SettingsRepository {
    val flow = MutableStateFlow(initial)
    override val settings = flow
    override suspend fun setTheme(theme: ThemeMode) {}
    override suspend fun setUse24h(value: Boolean) {}
    override suspend fun setShowQuote(value: Boolean) {}
    override suspend fun setColorfulIcons(value: Boolean) {}
    override suspend fun setHighContrast(value: Boolean) {}
    override suspend fun setGoalMinutes(minutes: Int) { flow.value = flow.value.copy(goalMinutes = minutes) }
    override suspend fun setLaunchDelaySeconds(seconds: Int) {}
    override suspend fun setOnboardingComplete(value: Boolean) {}
    override suspend fun setFavorites(favorites: List<String>) {}
    override suspend fun toggleDistracting(packageName: String) {}
}

private class FakeUsageRepository(private val totalMs: Long) : UsageRepository {
    override fun hasUsageAccess() = true
    override suspend fun getToday() = Result.Success(DailyUsage(totalMs, 0, emptyList()))
    override suspend fun getWeek() = Result.Success(WeekUsage(emptyList(), 0))
}

class GoalViewModelTest {

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `used minutes derived from today usage and limit from settings`() = runTest {
        val vm = GoalViewModel(
            settingsRepository = FakeSettingsRepository(UserSettings(goalMinutes = 150)),
            usageRepository = FakeUsageRepository(totalMs = 138 * 60_000L)
        )
        vm.state.test {
            val state = awaitItem()
            assertThat(state.usedMinutes).isEqualTo(138)
            assertThat(state.limitMinutes).isEqualTo(150)
        }
    }

    @Test
    fun `selecting a limit persists and updates state`() = runTest {
        val vm = GoalViewModel(
            settingsRepository = FakeSettingsRepository(UserSettings(goalMinutes = 150)),
            usageRepository = FakeUsageRepository(totalMs = 0)
        )
        vm.onAction(GoalAction.OnSelectLimit(240))
        vm.state.test {
            assertThat(awaitItem().limitMinutes).isEqualTo(240)
        }
    }
}
