package com.time.applauncher.addict.feature.agenda.data

import com.time.applauncher.addict.feature.agenda.data.db.AgendaDao
import com.time.applauncher.addict.feature.agenda.data.db.AgendaEventEntity
import com.time.applauncher.addict.feature.agenda.domain.AgendaDay
import com.time.applauncher.addict.feature.agenda.domain.AgendaEvent
import com.time.applauncher.addict.feature.agenda.domain.AgendaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomAgendaRepository(
    private val dao: AgendaDao
) : AgendaRepository {

    override fun observeAgenda(): Flow<List<AgendaDay>> =
        dao.observeEvents().map { rows ->
            // Query is ordered by (dayOrder, time), so groupBy keeps day groups
            // in dayOrder order and events in time order within each group.
            rows.groupBy { it.day }
                .map { (day, events) -> AgendaDay(day, events.map { it.toEvent() }) }
        }

    override suspend fun addEvent(day: String, time: String, title: String, meta: String) {
        val existingOrder = dao.dayOrderFor(day)
        val dayOrder = if (existingOrder >= 0) existingOrder else dao.nextDayOrder()
        dao.insert(
            AgendaEventEntity(day = day, dayOrder = dayOrder, time = time, title = title, meta = meta)
        )
    }

    override suspend fun deleteEvent(id: Long) = dao.delete(id)
}

private fun AgendaEventEntity.toEvent(): AgendaEvent =
    AgendaEvent(id = id, time = time, title = title, meta = meta)
