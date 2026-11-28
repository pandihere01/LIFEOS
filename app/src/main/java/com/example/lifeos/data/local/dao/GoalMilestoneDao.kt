
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeos.data.local.entity.GoalMilestoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalMilestoneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(
        milestone: GoalMilestoneEntity
    ): Long

    @Update
    suspend fun updateMilestone(
        milestone: GoalMilestoneEntity
    )

    @Delete
    suspend fun deleteMilestone(
        milestone: GoalMilestoneEntity
    )

    @Query("""
        SELECT * FROM goal_milestones
        WHERE goalId = :goalId
        ORDER BY createdAt ASC
    """)
    fun getMilestonesForGoal(
        goalId: Int
    ): Flow<List<GoalMilestoneEntity>>

    @Query("""
        UPDATE goal_milestones
        SET isCompleted = 1,
            completedAt = :completedAt
        WHERE id = :milestoneId
    """)
    suspend fun completeMilestone(
        milestoneId: Int,
        completedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        SELECT COUNT(*) FROM goal_milestones
        WHERE goalId = :goalId AND isCompleted = 1
    """)
    suspend fun getCompletedMilestoneCount(
        goalId: Int
    ): Int
}
