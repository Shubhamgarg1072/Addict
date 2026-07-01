package com.time.applauncher.addict.core.data.di

import com.time.applauncher.addict.core.data.apps.LauncherAppsRepository
import com.time.applauncher.addict.core.data.settings.DataStoreSettingsRepository
import com.time.applauncher.addict.core.data.usage.UsageStatsRepository
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.repository.UsageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreDataModule = module {
    single<SettingsRepository> { DataStoreSettingsRepository(androidContext()) }
    single<AppRepository> { LauncherAppsRepository(androidContext(), get()) }
    single<UsageRepository> { UsageStatsRepository(androidContext()) }
}
