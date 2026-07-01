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

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun count(): Int

    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Insert
    suspend fun insertItems(items: List<ChecklistItemEntity>)

    @Query("UPDATE checklist_items SET done = :done WHERE id = :itemId")
    suspend fun setItemDone(itemId: Long, done: Boolean)

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM notes")
    suspend fun nextNotePosition(): Int
}
