package com.time.applauncher.addict

import android.app.Application
import com.time.applauncher.addict.core.data.di.coreDataModule
import com.time.applauncher.addict.feature.agenda.data.di.agendaDataModule
import com.time.applauncher.addict.feature.agenda.presentation.agendaPresentationModule
import com.time.applauncher.addict.feature.home.presentation.homePresentationModule
import com.time.applauncher.addict.feature.notes.data.di.notesDataModule
import com.time.applauncher.addict.feature.notes.presentation.notesPresentationModule
import com.time.applauncher.addict.feature.onboarding.presentation.di.onboardingPresentationModule
import com.time.applauncher.addict.feature.settings.presentation.settingsPresentationModule
import com.time.applauncher.addict.feature.wellbeing.presentation.wellbeingPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StillApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StillApp)
            modules(
                // core
                coreDataModule,
                // feature data
                agendaDataModule,
                notesDataModule,
                // feature presentation
                onboardingPresentationModule,
                homePresentationModule,
                wellbeingPresentationModule,
                agendaPresentationModule,
                notesPresentationModule,
                settingsPresentationModule
            )
        }
    }
}
