package com.time.applauncher.addict.core.data.apps

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Process
import com.time.applauncher.addict.core.domain.model.AppInfo
import com.time.applauncher.addict.core.domain.repository.AppRepository
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.core.domain.util.DataError
import com.time.applauncher.addict.core.domain.util.EmptyResult
import com.time.applauncher.addict.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class LauncherAppsRepository(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : AppRepository {

    private val launcherApps: LauncherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    override fun observeApps(): Flow<List<AppInfo>> =
        settingsRepository.settings
            .map { it.distractingPackages }
            .distinctUntilChanged()
            .map { distracting -> queryLaunchable(distracting) }
            .flowOn(Dispatchers.IO)

    private fun queryLaunchable(distracting: Set<String>): List<AppInfo> {
        val self = context.packageName
        return launcherApps.getActivityList(null, Process.myUserHandle())
            .asSequence()
            .map { it.applicationInfo.packageName to it.label.toString() }
            .distinctBy { it.first }
            .filter { it.first != self }
            .map { (pkg, label) ->
                AppInfo(
                    packageName = pkg,
                    label = label,
                    isDistracting = pkg in distracting
                )
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    override fun launch(packageName: String): EmptyResult<DataError.Local> {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                ?: return Result.Error(DataError.Local.NOT_FOUND)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
