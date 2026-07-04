package com.time.applauncher.addict.feature.settings.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
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

class FavoritesViewModelTest {

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun apps(vararg names: String) =
        names.map { AppInfo(packageName = it, label = it.replaceFirstChar(Char::uppercase), isDistracting = false) }

    @Test
    fun `selection reflects persisted favorites`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(favorites = listOf("maps")))
        val vm = FavoritesViewModel(FakeAppRepository(apps("camera", "maps"), settings), settings)

        vm.state.test {
            val state = awaitItem()
            assertThat(state.apps.single { it.packageName == "maps" }.selected).isEqualTo(true)
            assertThat(state.apps.single { it.packageName == "camera" }.selected).isEqualTo(false)
            assertThat(state.selectedCount).isEqualTo(1)
        }
    }

    @Test
    fun `toggling adds and removes favorites`() = runTest {
        val settings = FakeSettingsRepository(UserSettings(favorites = listOf("maps")))
        val vm = FavoritesViewModel(FakeAppRepository(apps("camera", "maps"), settings), settings)

        vm.onAction(FavoritesAction.OnToggleApp("camera"))
        assertThat(settings.flow.value.favorites).containsExactly("maps", "camera")

        vm.onAction(FavoritesAction.OnToggleApp("maps"))
        assertThat(settings.flow.value.favorites).containsExactly("camera")
    }

    @Test
    fun `cannot add beyond the favorites limit`() = runTest {
        val packages = (1..MAX_FAVORITES).map { "app$it" }
        val settings = FakeSettingsRepository(UserSettings(favorites = packages))
        val vm = FavoritesViewModel(
            FakeAppRepository(apps(*(packages + "extra").toTypedArray()), settings),
            settings
        )

        vm.onAction(FavoritesAction.OnToggleApp("extra"))
        assertThat(settings.flow.value.favorites).isEqualTo(packages)
    }
}
