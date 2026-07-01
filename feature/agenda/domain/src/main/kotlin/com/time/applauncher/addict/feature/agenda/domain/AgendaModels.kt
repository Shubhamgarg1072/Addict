package com.time.applauncher.addict.feature.agenda.domain

data class AgendaEvent(
    val time: String,
    val title: String,
    val meta: String
)

data class AgendaDay(
    val day: String,
    val events: List<AgendaEvent>
)

interface AgendaRepository {
    fun getAgenda(): List<AgendaDay>
}
