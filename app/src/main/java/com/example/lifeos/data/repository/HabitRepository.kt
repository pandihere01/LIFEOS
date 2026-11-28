
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.dao.HabitDao
import com.example.lifeos.data.local.dao.HabitCompletionDao
import com.example.lifeos.data.local.entity.HabitEntity
import com.example.lifeos.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {

    fun getActiveHabits(): Flow<List<HabitEntity>> =
        habitDao.getActiveHabits()

    fun getAllHabits(): Flow<List<HabitEntity>> =
        habitDao.getAllHabits()

    suspend fun getHabitById(id: Int): HabitEntity? =
        habitDao.getHabitById(id)

    suspend fun addHabit(habit: HabitEntity): Long =
        habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: HabitEntity) =
        habitDao.updateHabit(habit)

    suspend fun archiveHabit(id: Int) =
        habitDao.archiveHabit(id)

    suspend fun deleteHabit(habit: HabitEntity) {
        completionDao.deleteCompletionsForHabit(habit.id)
        habitDao.deleteHabit(habit)
    }

    fun getHabitCompletions(
        habitId: Int
    ): Flow<List<HabitCompletionEntity>> =
        completionDao.getCompletionsForHabit(habitId)

    suspend fun getCompletionForDate(
        habitId: Int,
        date: String
    ): HabitCompletionEntity? =
        completionDao.getCompletionForDate(habitId, date)

    suspend fun recordCompletion(
        habitId: Int,
        date: String,
        completedValue: Double = 0.0,
        note: String = ""
    ): Boolean {

        val completion = HabitCompletionEntity(
            habitId = habitId,
            completionDate = date,
            completedValue = completedValue,
            note = note
        )

        val insertedId = completionDao.insertCompletion(completion)

        if (insertedId != -1L) {
            return true
        }

        return completionDao.updateCompletion(
            habitId = habitId,
            date = date,
            value = completedValue,
            note = note
        ) > 0
    }

    suspend fun undoCompletion(
        habitId: Int,
        date: String
    ) {
        completionDao.deleteCompletionForDate(
            habitId = habitId,
            date = date
        )
    }

    suspend fun getTotalCompletions(habitId: Int): Int =
        completionDao.getTotalCompletions(habitId)
}
