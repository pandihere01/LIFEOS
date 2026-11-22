
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeos.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Query("SELECT * FROM reminders ORDER BY reminderTime ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Int): ReminderEntity?

    @Query("""
        SELECT * FROM reminders
        WHERE isCompleted = 0
        ORDER BY reminderTime ASC
    """)
    fun getPendingReminders(): Flow<List<ReminderEntity>>

    @Query("""
        SELECT * FROM reminders
        WHERE isCompleted = 0
        AND isNotificationEnabled = 1
        AND reminderTime > :currentTime
        ORDER BY reminderTime ASC
    """)
    suspend fun getUpcomingReminders(
        currentTime: Long
    ): List<ReminderEntity>

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()
}
