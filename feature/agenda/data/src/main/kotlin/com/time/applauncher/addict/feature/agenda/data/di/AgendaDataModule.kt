package com.time.applauncher.addict.feature.agenda.data.di

import com.time.applauncher.addict.feature.agenda.data.MockAgendaRepository
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val agendaDataModule = module {
    singleOf(::MockAgendaRepository) { bind<AgendaRepository>() }
}
