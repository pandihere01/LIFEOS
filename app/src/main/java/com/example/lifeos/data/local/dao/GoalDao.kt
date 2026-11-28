
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeos.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("""
        SELECT * FROM goals
        WHERE isArchived = 0
        ORDER BY createdAt DESC
    """)
    fun getActiveGoals(): Flow<List<GoalEntity>>

    @Query("""
        SELECT * FROM goals
        ORDER BY createdAt DESC
    """)
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("""
        SELECT * FROM goals
        WHERE id = :goalId
        LIMIT 1
    """)
    suspend fun getGoalById(goalId: Int): GoalEntity?

    @Query("""
        UPDATE goals
        SET isArchived = 1
        WHERE id = :goalId
    """)
    suspend fun archiveGoal(goalId: Int)

    @Query("""
        UPDATE goals
        SET currentValue = :progress
        WHERE id = :goalId
    """)
    suspend fun updateGoalProgress(
        goalId: Int,
        progress: Double
    )

    @Query("""
        UPDATE goals
        SET isCompleted = 1
        WHERE id = :goalId
    """)
    suspend fun completeGoal(goalId: Int)
}
