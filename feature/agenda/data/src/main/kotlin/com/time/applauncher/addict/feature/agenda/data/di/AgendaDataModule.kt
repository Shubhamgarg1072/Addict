package com.time.applauncher.addict.feature.agenda.data.di

import androidx.room.Room
import com.time.applauncher.addict.feature.agenda.data.RoomAgendaRepository
import com.time.applauncher.addict.feature.agenda.data.db.AgendaDatabase
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val agendaDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AgendaDatabase::class.java,
            "still_agenda.db"
        ).build()
    }
    single { get<AgendaDatabase>().agendaDao() }
    singleOf(::RoomAgendaRepository) { bind<AgendaRepository>() }
}
