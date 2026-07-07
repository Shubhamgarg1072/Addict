package com.time.applauncher.addict.feature.notes.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class, ChecklistItemEntity::class, ChallengeEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun challengeDao(): ChallengeDao
}
