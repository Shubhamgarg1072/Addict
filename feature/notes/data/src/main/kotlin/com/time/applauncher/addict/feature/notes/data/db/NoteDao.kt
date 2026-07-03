package com.time.applauncher.addict.feature.notes.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Transaction
    @Query("SELECT * FROM notes ORDER BY pinned DESC, position ASC")
    fun observeNotesWithItems(): Flow<List<NoteWithItems>>

    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Insert
    suspend fun insertItem(item: ChecklistItemEntity): Long

    @Query("UPDATE checklist_items SET done = :done WHERE id = :itemId")
    suspend fun setItemDone(itemId: Long, done: Boolean)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    @Query("DELETE FROM checklist_items WHERE noteId = :noteId")
    suspend fun deleteItemsForNote(noteId: Long)

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM notes")
    suspend fun nextNotePosition(): Int

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM checklist_items WHERE noteId = :noteId")
    suspend fun nextItemPosition(noteId: Long): Int
}
