package com.time.applauncher.addict.feature.agenda.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.time.applauncher.addict.feature.agenda.domain.AgendaDay
import com.time.applauncher.addict.feature.agenda.domain.AgendaEvent
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private class FakeAgendaRepository : AgendaRepository {
    private var nextId = 2L
    val days = MutableStateFlow(
        listOf(
            AgendaDay("TODAY", listOf(AgendaEvent(1, "09:30", "Design review", "Room 3")))
        )
    )

    override fun observeAgenda(): Flow<List<AgendaDay>> = days

    override suspend fun addEvent(day: String, time: String, title: String, meta: String) {
        val event = AgendaEvent(nextId++, time, title, meta)
        days.update { list ->
            val existing = list.find { it.day == day }
            if (existing == null) {
                list + AgendaDay(day, listOf(event))
            } else {
                list.map { if (it.day == day) it.copy(events = it.events + event) else it }
            }
        }
    }

    override suspend fun deleteEvent(id: Long) {
        days.update { list ->
            list.map { it.copy(events = it.events.filterNot { e -> e.id == id }) }
        }
    }
}

class AgendaViewModelTest {

    @BeforeEach
    fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repository: AgendaRepository = FakeAgendaRepository()) =
        AgendaViewModel(repository, SavedStateHandle())

    @Test
    fun `saving a draft appends the event and closes the editor`() = runTest {
        val vm = viewModel()
        vm.onAction(AgendaAction.OnAddEvent)
        vm.onAction(AgendaAction.OnDraftTimeChange("13:00"))
        vm.onAction(AgendaAction.OnDraftTitleChange("Lunch"))
        vm.onAction(AgendaAction.OnDraftMetaChange("The Corner"))
        vm.onAction(AgendaAction.OnSaveEvent)
        vm.state.test {
            val state = awaitItem()
            val today = state.days.first { it.day == "TODAY" }
            assertThat(today.events.size).isEqualTo(2)
            assertThat(today.events.last().title).isEqualTo("Lunch")
            assertThat(state.isEditing).isFalse()
            assertThat(state.draftTitle).isEqualTo("")
        }
    }

    @Test
    fun `save is ignored while the draft is incomplete`() = runTest {
        val vm = viewModel()
        vm.onAction(AgendaAction.OnAddEvent)
        vm.onAction(AgendaAction.OnDraftTitleChange("No time yet"))
        vm.onAction(AgendaAction.OnSaveEvent)
        vm.state.test {
            val state = awaitItem()
            assertThat(state.days.first { it.day == "TODAY" }.events.size).isEqualTo(1)
            assertThat(state.isEditing).isTrue()
        }
    }

    @Test
    fun `deleting an event removes it`() = runTest {
        val vm = viewModel()
        vm.onAction(AgendaAction.OnDeleteEvent(1))
        vm.state.test {
            val today = awaitItem().days.first { it.day == "TODAY" }
            assertThat(today.events.size).isEqualTo(0)
        }
    }
}
