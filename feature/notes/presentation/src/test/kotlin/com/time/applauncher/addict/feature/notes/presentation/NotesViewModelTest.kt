package com.time.applauncher.addict.feature.notes.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.time.applauncher.addict.feature.notes.domain.ChecklistItem
import com.time.applauncher.addict.feature.notes.domain.Note
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
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

private class FakeNoteRepository : NoteRepository {
    val notes = MutableStateFlow(
        listOf(Note(1, "Reading list", true, null, listOf(ChecklistItem(10, "Deep Work", false))))
    )
    override fun observeNotes(): Flow<List<Note>> = notes
    override suspend fun ensureSeeded() {}
    override suspend fun setItemDone(itemId: Long, done: Boolean) {
        notes.update { list ->
            list.map { note ->
                note.copy(items = note.items.map { if (it.id == itemId) it.copy(done = done) else it })
            }
        }
    }
    override suspend fun addNote(title: String) {
        notes.update { it + Note(it.size + 1L, title, false, null, emptyList()) }
    }
}

class NotesViewModelTest {

    @BeforeEach
    fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `toggling an item updates its done state`() = runTest {
        val vm = NotesViewModel(FakeNoteRepository())
        vm.onAction(NotesAction.OnToggleItem(itemId = 10, done = true))
        vm.state.test {
            val state = awaitItem()
            assertThat(state.notes.first().items.first().done).isTrue()
        }
    }

    @Test
    fun `adding a note appends it to the list`() = runTest {
        val vm = NotesViewModel(FakeNoteRepository())
        vm.onAction(NotesAction.OnAddNote)
        vm.state.test {
            assertThat(awaitItem().notes.size).isEqualTo(2)
        }
    }
}
