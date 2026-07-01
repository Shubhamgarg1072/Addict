package com.time.applauncher.addict.feature.notes.data

import com.time.applauncher.addict.feature.notes.data.db.ChecklistItemEntity
import com.time.applauncher.addict.feature.notes.data.db.NoteDao
import com.time.applauncher.addict.feature.notes.data.db.NoteEntity
import com.time.applauncher.addict.feature.notes.data.db.NoteWithItems
import com.time.applauncher.addict.feature.notes.domain.ChecklistItem
import com.time.applauncher.addict.feature.notes.domain.Note
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNoteRepository(
    private val dao: NoteDao
) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> =
        dao.observeNotesWithItems().map { list -> list.map { it.toNote() } }

    override suspend fun ensureSeeded() {
        if (dao.count() > 0) return
        seedNote(
            "Reading list", pinned = true, position = 0, body = null,
            items = listOf("Deep Work — Newport" to true, "Digital Minimalism" to false, "The Shallows" to false)
        )
        seedNote(
            "This week", pinned = false, position = 1, body = null,
            items = listOf("Cancel two subscriptions" to true, "Call Dad" to false)
        )
        seedNote(
            "Idea", pinned = false, position = 2,
            body = "A phone that asks “why?” before it says “yes.”",
            items = emptyList()
        )
    }

    override suspend fun setItemDone(itemId: Long, done: Boolean) = dao.setItemDone(itemId, done)

    override suspend fun addNote(title: String) {
        dao.insertNote(
            NoteEntity(title = title, pinned = false, body = null, position = dao.nextNotePosition())
        )
    }

    private suspend fun seedNote(
        title: String,
        pinned: Boolean,
        position: Int,
        body: String?,
        items: List<Pair<String, Boolean>>
    ) {
        val noteId = dao.insertNote(NoteEntity(title = title, pinned = pinned, body = body, position = position))
        if (items.isNotEmpty()) {
            dao.insertItems(
                items.mapIndexed { index, (text, done) ->
                    ChecklistItemEntity(noteId = noteId, text = text, done = done, position = index)
                }
            )
        }
    }
}

private fun NoteWithItems.toNote(): Note = Note(
    id = note.id,
    title = note.title,
    pinned = note.pinned,
    body = note.body,
    items = items.sortedBy { it.position }.map { ChecklistItem(it.id, it.text, it.done) }
)
