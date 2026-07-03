package com.time.applauncher.addict.feature.notes.domain

import kotlinx.coroutines.flow.Flow

data class ChecklistItem(
    val id: Long,
    val text: String,
    val done: Boolean
)

data class Note(
    val id: Long,
    val title: String,
    val pinned: Boolean,
    val body: String?,
    val items: List<ChecklistItem>
)

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>
    suspend fun setItemDone(itemId: Long, done: Boolean)
    suspend fun addNote(title: String, body: String?)
    suspend fun addItem(noteId: Long, text: String)
    suspend fun deleteNote(noteId: Long)
}
