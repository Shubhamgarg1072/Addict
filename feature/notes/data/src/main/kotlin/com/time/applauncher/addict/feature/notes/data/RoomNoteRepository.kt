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

    override suspend fun setItemDone(itemId: Long, done: Boolean) = dao.setItemDone(itemId, done)

    override suspend fun addNote(title: String, body: String?, pinned: Boolean): Long =
        dao.insertNote(
            NoteEntity(title = title, pinned = pinned, body = body, position = dao.nextNotePosition())
        )

    override suspend fun addItem(noteId: Long, text: String): Long =
        dao.insertItem(
            ChecklistItemEntity(
                noteId = noteId,
                text = text,
                done = false,
                position = dao.nextItemPosition(noteId)
            )
        )

    override suspend fun deleteNote(noteId: Long) {
        dao.deleteItemsForNote(noteId)
        dao.deleteNote(noteId)
    }
}

private fun NoteWithItems.toNote(): Note = Note(
    id = note.id,
    title = note.title,
    pinned = note.pinned,
    body = note.body,
    items = items.sortedBy { it.position }.map { ChecklistItem(it.id, it.text, it.done) }
)
