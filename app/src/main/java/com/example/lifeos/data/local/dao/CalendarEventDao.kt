
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeos.data.local.entity.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity)

    @Query("SELECT * FROM calendar_events ORDER BY startTime ASC")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    @Query("""
        SELECT * FROM calendar_events
        WHERE startTime >= :startOfDay
        AND startTime < :endOfDay
        ORDER BY startTime ASC
    """)
    fun getEventsForDay(
        startOfDay: Long,
        endOfDay: Long
    ): Flow<List<CalendarEventEntity>>

    @Update
    suspend fun updateEvent(event: CalendarEventEntity)

    @Delete
    suspend fun deleteEvent(event: CalendarEventEntity)

    @Query("DELETE FROM calendar_events")
    suspend fun deleteAllEvents()
}
