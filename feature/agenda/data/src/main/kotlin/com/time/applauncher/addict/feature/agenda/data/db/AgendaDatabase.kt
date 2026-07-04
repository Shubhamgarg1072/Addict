package com.time.applauncher.addict.feature.agenda.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AgendaEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AgendaDatabase : RoomDatabase() {
    abstract fun agendaDao(): AgendaDao
}
