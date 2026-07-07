package com.time.applauncher.addict.feature.agenda.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agenda_events")
data class AgendaEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: String,
    val dayOrder: Int,
    val time: String,
    val title: String,
    val meta: String
)
