package com.time.applauncher.addict.feature.settings.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.time.applauncher.addict.core.domain.model.AppInfo
import com.time.applauncher.addict.core.domain.model.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DistractingAppsViewModelTest {

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `gated flags come from the persisted distracting set`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(distractingPackages = setOf("insta")))
        val appRepository = FakeAppRepository(
            listOf(
                AppInfo("insta", "Instagram", isDistracting = false),
                AppInfo("maps", "Maps", isDistracting = false)
            ),
            settings
        )
        val vm = DistractingAppsViewModel(appRepository, settings)

        vm.state.test {
            val state = awaitItem()
            assertThat(state.apps.single { it.packageName == "insta" }.gated).isEqualTo(true)
            assertThat(state.apps.single { it.packageName == "maps" }.gated).isEqualTo(false)
            assertThat(state.gatedCount).isEqualTo(1)
        }
    }

    @Test
    fun `toggling an app flips its gate and updates state`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(distractingPackages = setOf("insta")))
        val appRepository = FakeAppRepository(
            listOf(AppInfo("insta", "Instagram", false), AppInfo("maps", "Maps", false)),
            settings
        )
        val vm = DistractingAppsViewModel(appRepository, settings)

        vm.onAction(DistractingAppsAction.OnToggleApp("maps"))
        assertThat(settings.flow.value.distractingPackages).isEqualTo(setOf("insta", "maps"))

        vm.state.test {
            assertThat(awaitItem().apps.single { it.packageName == "maps" }.gated).isEqualTo(true)
        }

        vm.onAction(DistractingAppsAction.OnToggleApp("insta"))
        assertThat(settings.flow.value.distractingPackages).isEqualTo(setOf("maps"))
    }
}
