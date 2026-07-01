package com.time.applauncher.addict.feature.home.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.time.applauncher.addict.core.domain.model.AppInfo
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.util.Result
import com.time.applauncher.addict.feature.home.presentation.search.SearchAction
import com.time.applauncher.addict.feature.home.presentation.search.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private class FakeAppRepository(private val apps: List<AppInfo>) : AppRepository {
    var launched: String? = null
    override fun observeApps(): Flow<List<AppInfo>> = flowOf(apps)
    override fun launch(packageName: String) = Result.Success(Unit).also { launched = packageName }
}

class SearchViewModelTest {

    private val apps = listOf(
        AppInfo("c.calc", "Calculator", false),
        AppInfo("c.cam", "Camera", false),
        AppInfo("i.ig", "Instagram", true)
    )

    @BeforeEach
    fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `empty query lists all apps sorted with section letters and mindful flag`() = runTest {
        val vm = SearchViewModel(FakeAppRepository(apps))
        vm.state.test {
            val state = awaitItem()
            assertThat(state.results.map { it.name }).containsExactly("Calculator", "Camera", "Instagram")
            // First 'C' shows a letter header, second 'C' does not, 'I' shows one.
            assertThat(state.results[0].showLetter).isTrue()
            assertThat(state.results[1].showLetter).isEqualTo(false)
            assertThat(state.results[2].showLetter).isTrue()
            assertThat(state.results[2].isDistracting).isTrue()
        }
    }

    @Test
    fun `typing filters results by label`() = runTest {
        val vm = SearchViewModel(FakeAppRepository(apps))
        vm.onAction(SearchAction.OnKey('c'))
        vm.onAction(SearchAction.OnKey('a'))
        vm.onAction(SearchAction.OnKey('m'))
        vm.state.test {
            val state = awaitItem()
            assertThat(state.results.map { it.name }).containsExactly("Camera")
        }
    }
}
