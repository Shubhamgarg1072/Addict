package com.time.applauncher.addict.feature.agenda.data

import com.time.applauncher.addict.feature.agenda.domain.AgendaDay
import com.time.applauncher.addict.feature.agenda.domain.AgendaEvent
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository

class MockAgendaRepository : AgendaRepository {
    override fun getAgenda(): List<AgendaDay> = listOf(
        AgendaDay(
            day = "TODAY",
            events = listOf(
                AgendaEvent("09:30", "Design review", "45 min · Room 3"),
                AgendaEvent("13:00", "Lunch with Mara", "The Corner"),
                AgendaEvent("16:30", "Dentist", "Reminder")
            )
        ),
        AgendaDay(
            day = "TOMORROW",
            events = listOf(
                AgendaEvent("08:00", "Morning run", "5 km"),
                AgendaEvent("11:00", "1:1 with Sam", "30 min")
            )
        )
    )
}
