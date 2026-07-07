package com.time.applauncher.addict.feature.agenda.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaDao {

    @Query("SELECT * FROM agenda_events ORDER BY dayOrder ASC, time ASC")
    fun observeEvents(): Flow<List<AgendaEventEntity>>

    @Insert
    suspend fun insert(event: AgendaEventEntity): Long

    @Query("DELETE FROM agenda_events WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COALESCE(MAX(dayOrder), -1) FROM agenda_events WHERE day = :day")
    suspend fun dayOrderFor(day: String): Int

    @Query("SELECT COALESCE(MAX(dayOrder), -1) + 1 FROM agenda_events")
    suspend fun nextDayOrder(): Int
}
