
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lifeos.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(
        completion: HabitCompletionEntity
    ): Long

    @Query("""
        SELECT * FROM habit_completions
        WHERE habitId = :habitId
        ORDER BY completionDate DESC
    """)
    fun getCompletionsForHabit(
        habitId: Int
    ): Flow<List<HabitCompletionEntity>>

    @Query("""
        SELECT * FROM habit_completions
        WHERE habitId = :habitId
        AND completionDate = :date
        LIMIT 1
    """)
    suspend fun getCompletionForDate(
        habitId: Int,
        date: String
    ): HabitCompletionEntity?

    @Query("""
        SELECT * FROM habit_completions
        WHERE completionDate = :date
        ORDER BY createdAt DESC
    """)
    fun getCompletionsForDate(
        date: String
    ): Flow<List<HabitCompletionEntity>>

    @Query("""
        SELECT COUNT(*) FROM habit_completions
        WHERE habitId = :habitId
    """)
    suspend fun getTotalCompletions(
        habitId: Int
    ): Int

    @Query("""
        DELETE FROM habit_completions
        WHERE habitId = :habitId
    """)
    suspend fun deleteCompletionsForHabit(
        habitId: Int
    )

    @Query("""
        DELETE FROM habit_completions
        WHERE habitId = :habitId
        AND completionDate = :date
    """)
    suspend fun deleteCompletionForDate(
        habitId: Int,
        date: String
    )

    @Query("""
        UPDATE habit_completions
        SET completedValue = :value, note = :note
        WHERE habitId = :habitId
        AND completionDate = :date
    """)
    suspend fun updateCompletion(
        habitId: Int,
        date: String,
        value: Double,
        note: String
    ): Int
}
