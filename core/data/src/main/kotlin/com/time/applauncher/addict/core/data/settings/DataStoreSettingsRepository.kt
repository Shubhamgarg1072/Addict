package com.time.applauncher.addict.core.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.model.UserSettings
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "still_settings")

/** Default set of apps that trigger the intentional-launch gate. */
val DEFAULT_DISTRACTING_PACKAGES = setOf(
    "com.instagram.android",
    "com.facebook.katana",
    "com.zhiliaoapp.musically",   // TikTok
    "com.google.android.youtube",
    "com.reddit.frontpage",
    "com.snapchat.android",
    "com.twitter.android",
    "com.x.android"
)

/** Package names shown as home favorites by default (filtered to installed apps at runtime). */
val DEFAULT_FAVORITE_PACKAGES = listOf(
    "com.google.android.apps.messaging",
    "com.google.android.dialer",
    "com.android.dialer",
    "com.google.android.GoogleCamera",
    "com.android.camera2",
    "com.google.android.apps.maps",
    "com.spotify.music",
    "com.whatsapp"
)

class DataStoreSettingsRepository(
    private val context: Context
) : SettingsRepository {

    private object Keys {
        val theme = stringPreferencesKey("theme")
        val use24h = booleanPreferencesKey("use_24h")
        val showQuote = booleanPreferencesKey("show_quote")
        val colorfulIcons = booleanPreferencesKey("colorful_icons")
        val highContrast = booleanPreferencesKey("high_contrast")
        val goalMinutes = intPreferencesKey("goal_minutes")
        val launchDelay = intPreferencesKey("launch_delay_seconds")
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
        val favorites = stringPreferencesKey("favorites")
        val distracting = stringSetPreferencesKey("distracting_packages")
    }

    override val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            theme = prefs[Keys.theme]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.AMOLED,
            use24h = prefs[Keys.use24h] ?: false,
            showQuote = prefs[Keys.showQuote] ?: true,
            colorfulIcons = prefs[Keys.colorfulIcons] ?: false,
            highContrast = prefs[Keys.highContrast] ?: false,
            goalMinutes = prefs[Keys.goalMinutes] ?: 150,
            launchDelaySeconds = prefs[Keys.launchDelay] ?: 5,
            onboardingComplete = prefs[Keys.onboardingComplete] ?: false,
            favorites = prefs[Keys.favorites]?.takeIf { it.isNotEmpty() }?.split("\n")
                ?: emptyList(),
            distractingPackages = prefs[Keys.distracting] ?: DEFAULT_DISTRACTING_PACKAGES
        )
    }

    override suspend fun setTheme(theme: ThemeMode) = edit { it[Keys.theme] = theme.name }
    override suspend fun setUse24h(value: Boolean) = edit { it[Keys.use24h] = value }
    override suspend fun setShowQuote(value: Boolean) = edit { it[Keys.showQuote] = value }
    override suspend fun setColorfulIcons(value: Boolean) = edit { it[Keys.colorfulIcons] = value }
    override suspend fun setHighContrast(value: Boolean) = edit { it[Keys.highContrast] = value }
    override suspend fun setGoalMinutes(minutes: Int) = edit { it[Keys.goalMinutes] = minutes }
    override suspend fun setLaunchDelaySeconds(seconds: Int) = edit { it[Keys.launchDelay] = seconds }
    override suspend fun setOnboardingComplete(value: Boolean) =
        edit { it[Keys.onboardingComplete] = value }

    override suspend fun setFavorites(favorites: List<String>) =
        edit { it[Keys.favorites] = favorites.joinToString("\n") }

    override suspend fun toggleDistracting(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.distracting] ?: DEFAULT_DISTRACTING_PACKAGES
            prefs[Keys.distracting] =
                if (packageName in current) current - packageName else current + packageName
        }
    }

    private suspend inline fun edit(crossinline block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.dataStore.edit { block(it) }
    }
}
