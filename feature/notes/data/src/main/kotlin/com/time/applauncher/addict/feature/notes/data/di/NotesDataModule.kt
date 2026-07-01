package com.time.applauncher.addict.feature.notes.data.di

import androidx.room.Room
import com.time.applauncher.addict.feature.notes.data.RoomNoteRepository
import com.time.applauncher.addict.feature.notes.data.db.NotesDatabase
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val notesDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            NotesDatabase::class.java,
            "still_notes.db"
        ).build()
    }
    single { get<NotesDatabase>().noteDao() }
    singleOf(::RoomNoteRepository) { bind<NoteRepository>() }
}
