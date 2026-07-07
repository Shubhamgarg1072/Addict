package com.time.applauncher.addict.feature.settings.presentation.backup

import android.content.Context
import android.net.Uri
import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.repository.SettingsRepository
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository
import com.time.applauncher.addict.feature.notes.domain.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BackupSettings(
    val theme: String,
    val use24h: Boolean,
    val showQuote: Boolean,
    val colorfulIcons: Boolean,
    val highContrast: Boolean,
    val goalMinutes: Int,
    val launchDelaySeconds: Int,
    val favorites: List<String>,
    val distractingPackages: List<String>
)

@Serializable
data class BackupChecklistItem(val text: String, val done: Boolean)

@Serializable
data class BackupNote(
    val title: String,
    val pinned: Boolean,
    val body: String?,
    val items: List<BackupChecklistItem>
)

@Serializable
data class BackupAgendaEvent(val day: String, val time: String, val title: String, val meta: String)

@Serializable
data class StillBackup(
    val version: Int = 1,
    val settings: BackupSettings,
    val notes: List<BackupNote>,
    val agenda: List<BackupAgendaEvent>
)

interface BackupService {
    /** @return false if the backup could not be written. */
    suspend fun backupTo(uri: Uri): Boolean

    /** @return false if the file could not be read or parsed. */
    suspend fun restoreFrom(uri: Uri): Boolean
}

/**
 * Exports/imports all user data (settings, notes, agenda) as JSON through the
 * Storage Access Framework. Restore overwrites settings and appends notes/agenda.
 */
class SafBackupService(
    private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val noteRepository: NoteRepository,
    private val agendaRepository: AgendaRepository
) : BackupService {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override suspend fun backupTo(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val settings = settingsRepository.settings.first()
            val notes = noteRepository.observeNotes().first()
            val agendaDays = agendaRepository.observeAgenda().first()

            val backup = StillBackup(
                settings = BackupSettings(
                    theme = settings.theme.name,
                    use24h = settings.use24h,
                    showQuote = settings.showQuote,
                    colorfulIcons = settings.colorfulIcons,
                    highContrast = settings.highContrast,
                    goalMinutes = settings.goalMinutes,
                    launchDelaySeconds = settings.launchDelaySeconds,
                    favorites = settings.favorites,
                    distractingPackages = settings.distractingPackages.toList()
                ),
                notes = notes.map { note ->
                    BackupNote(
                        title = note.title,
                        pinned = note.pinned,
                        body = note.body,
                        items = note.items.map { BackupChecklistItem(it.text, it.done) }
                    )
                },
                agenda = agendaDays.flatMap { day ->
                    day.events.map { BackupAgendaEvent(day.day, it.time, it.title, it.meta) }
                }
            )

            val stream = context.contentResolver.openOutputStream(uri, "wt")
                ?: error("Cannot open $uri for writing")
            stream.use { it.write(json.encodeToString(backup).toByteArray()) }
        }.isSuccess
    }

    override suspend fun restoreFrom(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val stream = context.contentResolver.openInputStream(uri)
                ?: error("Cannot open $uri for reading")
            val backup = json.decodeFromString<StillBackup>(
                stream.use { it.readBytes().decodeToString() }
            )

            with(backup.settings) {
                settingsRepository.setTheme(
                    runCatching { ThemeMode.valueOf(theme) }.getOrDefault(ThemeMode.AMOLED)
                )
                settingsRepository.setUse24h(use24h)
                settingsRepository.setShowQuote(showQuote)
                settingsRepository.setColorfulIcons(colorfulIcons)
                settingsRepository.setHighContrast(highContrast)
                settingsRepository.setGoalMinutes(goalMinutes)
                settingsRepository.setLaunchDelaySeconds(launchDelaySeconds)
                settingsRepository.setFavorites(favorites)

                val target = distractingPackages.toSet()
                val current = settingsRepository.settings.first().distractingPackages
                ((current - target) + (target - current)).forEach {
                    settingsRepository.toggleDistracting(it)
                }
            }
            // A restored device has been through onboarding.
            settingsRepository.setOnboardingComplete(true)

            backup.notes.forEach { note ->
                val noteId = noteRepository.addNote(note.title, note.body, note.pinned)
                note.items.forEach { item ->
                    val itemId = noteRepository.addItem(noteId, item.text)
                    if (item.done) noteRepository.setItemDone(itemId, true)
                }
            }
            backup.agenda.forEach {
                agendaRepository.addEvent(it.day, it.time, it.title, it.meta)
            }
        }.isSuccess
    }
}
