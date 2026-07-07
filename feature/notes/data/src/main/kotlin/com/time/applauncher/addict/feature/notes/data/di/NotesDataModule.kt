package com.time.applauncher.addict.feature.notes.data.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.time.applauncher.addict.feature.notes.data.RoomChallengeRepository
import com.time.applauncher.addict.feature.notes.data.RoomNoteRepository
import com.time.applauncher.addict.feature.notes.data.db.NotesDatabase
import com.time.applauncher.addict.feature.notes.domain.ChallengeRepository
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** Adds the user-created challenges table without touching existing notes. */
private val MIGRATION_1_2 = Migration(1, 2) { db: SupportSQLiteDatabase ->
    db.execSQL(
        "CREATE TABLE IF NOT EXISTS challenges (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT NOT NULL, " +
            "shortLabel TEXT NOT NULL, " +
            "streak INTEGER NOT NULL, " +
            "targetDays INTEGER NOT NULL, " +
            "weekMask INTEGER NOT NULL, " +
            "reminder TEXT, " +
            "position INTEGER NOT NULL)"
    )
}

val notesDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NotesDatabase::class.java,
            "still_notes.db"
        ).addMigrations(MIGRATION_1_2).build()
    }
    single { get<NotesDatabase>().noteDao() }
    single { get<NotesDatabase>().challengeDao() }
    singleOf(::RoomNoteRepository) { bind<NoteRepository>() }
    singleOf(::RoomChallengeRepository) { bind<ChallengeRepository>() }
}
