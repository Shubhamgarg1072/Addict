package com.time.applauncher.addict.feature.settings.presentation

import android.net.Uri
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.model.UserSettings
import com.time.applauncher.addict.feature.settings.presentation.backup.BackupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private class FakeBackupService : BackupService {
    override suspend fun backupTo(uri: Uri) = true
    override suspend fun restoreFrom(uri: Uri) = true
}

class SettingsViewModelTest {

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(settings: FakeSettingsRepository) =
        SettingsViewModel(settingsRepository = settings, backupService = FakeBackupService())

    @Test
    fun `cycling theme walks through all modes and persists`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(theme = ThemeMode.AMOLED))
        val vm = viewModel(settings)

        vm.onAction(SettingsAction.CycleTheme)
        assertThat(settings.flow.value.theme).isEqualTo(ThemeMode.DARK)

        vm.onAction(SettingsAction.CycleTheme)
        assertThat(settings.flow.value.theme).isEqualTo(ThemeMode.LIGHT)

        vm.onAction(SettingsAction.CycleTheme)
        assertThat(settings.flow.value.theme).isEqualTo(ThemeMode.AMOLED)
    }

    @Test
    fun `cycling launch delay moves to next option and wraps`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(launchDelaySeconds = 5))
        val vm = viewModel(settings)

        vm.onAction(SettingsAction.CycleLaunchDelay)
        assertThat(settings.flow.value.launchDelaySeconds).isEqualTo(10)

        settings.setLaunchDelaySeconds(30)
        vm.onAction(SettingsAction.CycleLaunchDelay)
        assertThat(settings.flow.value.launchDelaySeconds).isEqualTo(3)
    }

    @Test
    fun `toggles flip persisted values`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(use24h = false, highContrast = false))
        val vm = viewModel(settings)

        vm.onAction(SettingsAction.Toggle24h)
        vm.onAction(SettingsAction.ToggleHighContrast)

        assertThat(settings.flow.value.use24h).isEqualTo(true)
        assertThat(settings.flow.value.highContrast).isEqualTo(true)
    }

    @Test
    fun `state mirrors settings updates`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(goalMinutes = 150))
        val vm = viewModel(settings)

        vm.state.test {
            assertThat(awaitItem().settings.goalMinutes).isEqualTo(150)
            settings.setGoalMinutes(240)
            assertThat(awaitItem().settings.goalMinutes).isEqualTo(240)
        }
    }
}
