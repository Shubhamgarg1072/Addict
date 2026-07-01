package com.time.applauncher.addict.feature.notes.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val pinned: Boolean,
    val body: String?,
    val position: Int
)

@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val text: String,
    val done: Boolean,
    val position: Int
)

data class NoteWithItems(
    @Embedded val note: NoteEntity,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val items: List<ChecklistItemEntity>
)
