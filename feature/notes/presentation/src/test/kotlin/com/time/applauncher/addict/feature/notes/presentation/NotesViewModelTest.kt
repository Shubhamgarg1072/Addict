package com.time.applauncher.addict.feature.notes.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
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
    private var nextNoteId = 2L
    private var nextItemId = 2L
    val notes = MutableStateFlow(
        listOf(
            Note(1, "Reading list", pinned = false, body = null, items = listOf(ChecklistItem(1, "Deep Work", false)))
        )
    )

    override fun observeNotes(): Flow<List<Note>> = notes

    override suspend fun setItemDone(itemId: Long, done: Boolean) {
        notes.update { list ->
            list.map { note ->
                note.copy(items = note.items.map { if (it.id == itemId) it.copy(done = done) else it })
            }
        }
    }

    override suspend fun addNote(title: String, body: String?, pinned: Boolean): Long {
        val id = nextNoteId++
        notes.update { it + Note(id, title, pinned = pinned, body = body, items = emptyList()) }
        return id
    }

    override suspend fun addItem(noteId: Long, text: String): Long {
        val id = nextItemId++
        notes.update { list ->
            list.map { note ->
                if (note.id == noteId) {
                    note.copy(items = note.items + ChecklistItem(id, text, false))
                } else note
            }
        }
        return id
    }

    override suspend fun deleteNote(noteId: Long) {
        notes.update { list -> list.filterNot { it.id == noteId } }
    }
}

class NotesViewModelTest {

    @BeforeEach
    fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterEach
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(repository: NoteRepository = FakeNoteRepository()) =
        NotesViewModel(repository, SavedStateHandle())

    @Test
    fun `saving a note draft appends the note and closes the editor`() = runTest {
        val vm = viewModel()
        vm.onAction(NotesAction.OnAddNote)
        vm.onAction(NotesAction.OnDraftTitleChange("Groceries"))
        vm.onAction(NotesAction.OnDraftBodyChange("Milk, eggs"))
        vm.onAction(NotesAction.OnSaveNote)
        vm.state.test {
            val state = awaitItem()
            assertThat(state.notes.size).isEqualTo(2)
            assertThat(state.notes.last().title).isEqualTo("Groceries")
            assertThat(state.notes.last().body).isEqualTo("Milk, eggs")
            assertThat(state.isEditing).isFalse()
        }
    }

    @Test
    fun `save is ignored while the note title is blank`() = runTest {
        val vm = viewModel()
        vm.onAction(NotesAction.OnAddNote)
        vm.onAction(NotesAction.OnSaveNote)
        vm.state.test {
            val state = awaitItem()
            assertThat(state.notes.size).isEqualTo(1)
            assertThat(state.isEditing).isTrue()
        }
    }

    @Test
    fun `adding a checklist item appends it to the note`() = runTest {
        val vm = viewModel()
        vm.onAction(NotesAction.OnStartAddItem(1))
        vm.onAction(NotesAction.OnDraftItemChange("The Shallows"))
        vm.onAction(NotesAction.OnSaveItem)
        vm.state.test {
            val state = awaitItem()
            val note = state.notes.first { it.id == 1L }
            assertThat(note.items.size).isEqualTo(2)
            assertThat(note.items.last().text).isEqualTo("The Shallows")
            assertThat(state.addingItemNoteId).isNull()
        }
    }

    @Test
    fun `toggling an item flips done`() = runTest {
        val vm = viewModel()
        vm.onAction(NotesAction.OnToggleItem(1, true))
        vm.state.test {
            val note = awaitItem().notes.first { it.id == 1L }
            assertThat(note.items.first().done).isTrue()
        }
    }

    @Test
    fun `deleting a note removes it`() = runTest {
        val vm = viewModel()
        vm.onAction(NotesAction.OnDeleteNote(1))
        vm.state.test {
            assertThat(awaitItem().notes.size).isEqualTo(0)
        }
    }
}
