package com.time.applauncher.addict.core.data.usage

import android.content.Context
import android.content.Intent
import android.provider.Settings

/** Helper to open the system Usage Access settings screen. */
object UsagePermission {
    fun launchSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
