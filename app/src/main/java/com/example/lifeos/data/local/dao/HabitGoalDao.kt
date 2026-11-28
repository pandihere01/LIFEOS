
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lifeos.data.local.entity.HabitGoalCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitGoalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkHabitToGoal(
        crossRef: HabitGoalCrossRef
    ): Long

    @Query("""
        DELETE FROM habit_goal_cross_ref
        WHERE habitId = :habitId AND goalId = :goalId
    """)
    suspend fun unlinkHabitFromGoal(
        habitId: Int,
        goalId: Int
    )

    @Query("""
        SELECT habitId FROM habit_goal_cross_ref
        WHERE goalId = :goalId
    """)
    fun getHabitIdsForGoal(
        goalId: Int
    ): Flow<List<Int>>

    @Query("""
        SELECT goalId FROM habit_goal_cross_ref
        WHERE habitId = :habitId
    """)
    fun getGoalIdsForHabit(
        habitId: Int
    ): Flow<List<Int>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM habit_goal_cross_ref
            WHERE habitId = :habitId AND goalId = :goalId
        )
    """)
    suspend fun isHabitLinkedToGoal(
        habitId: Int,
        goalId: Int
    ): Boolean
}
