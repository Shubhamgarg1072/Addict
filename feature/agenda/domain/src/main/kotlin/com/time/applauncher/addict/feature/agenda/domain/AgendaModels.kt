package com.time.applauncher.addict.feature.agenda.domain

import kotlinx.coroutines.flow.Flow

data class AgendaEvent(
    val id: Long,
    val time: String,
    val title: String,
    val meta: String
)

data class AgendaDay(
    val day: String,
    val events: List<AgendaEvent>
)

interface AgendaRepository {
    fun observeAgenda(): Flow<List<AgendaDay>>
    suspend fun addEvent(day: String, time: String, title: String, meta: String)
    suspend fun deleteEvent(id: Long)
}
