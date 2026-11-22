
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.dao.CalendarEventDao
import com.example.lifeos.data.local.entity.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

class CalendarRepository(
    private val calendarEventDao: CalendarEventDao
) {

    fun getAllEvents(): Flow<List<CalendarEventEntity>> {
        return calendarEventDao.getAllEvents()
    }

    fun getEventsForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<List<CalendarEventEntity>> {
        return calendarEventDao.getEventsForDay(startOfDay, endOfDay)
    }

    suspend fun addEvent(event: CalendarEventEntity) {
        calendarEventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: CalendarEventEntity) {
        calendarEventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: CalendarEventEntity) {
        calendarEventDao.deleteEvent(event)
    }
}
