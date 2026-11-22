
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.dao.ReminderDao
import com.example.lifeos.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val reminderDao: ReminderDao
) {

    fun getAllReminders(): Flow<List<ReminderEntity>> {
        return reminderDao.getAllReminders()
    }

    fun getPendingReminders(): Flow<List<ReminderEntity>> {
        return reminderDao.getPendingReminders()
    }

    suspend fun getUpcomingReminders(currentTime: Long): List<ReminderEntity> {
        return reminderDao.getUpcomingReminders(currentTime)
    }

    suspend fun addReminder(reminder: ReminderEntity): Long {
        return reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: ReminderEntity) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: ReminderEntity) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun getReminderById(id: Int): ReminderEntity? {
        return reminderDao.getReminderById(id)
    }
}
