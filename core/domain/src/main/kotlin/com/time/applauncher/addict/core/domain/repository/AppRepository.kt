package com.time.applauncher.addict.core.domain.repository

import com.time.applauncher.addict.core.domain.model.AppInfo
import com.time.applauncher.addict.core.domain.util.DataError
import com.time.applauncher.addict.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    /** All launchable apps, flagged with the user's distracting set, sorted by label. */
    fun observeApps(): Flow<List<AppInfo>>

    /** Launch an installed app by package name. */
    fun launch(packageName: String): EmptyResult<DataError.Local>
}
