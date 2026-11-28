
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.dao.GoalDao
import com.example.lifeos.data.local.dao.GoalMilestoneDao
import com.example.lifeos.data.local.dao.HabitGoalDao
import com.example.lifeos.data.local.entity.GoalEntity
import com.example.lifeos.data.local.entity.GoalMilestoneEntity
import com.example.lifeos.data.local.entity.HabitGoalCrossRef
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao,
    private val milestoneDao: GoalMilestoneDao,
    private val habitGoalDao: HabitGoalDao
) {

    fun getActiveGoals(): Flow<List<GoalEntity>> =
        goalDao.getActiveGoals()

    fun getAllGoals(): Flow<List<GoalEntity>> =
        goalDao.getAllGoals()

    suspend fun getGoalById(id: Int): GoalEntity? =
        goalDao.getGoalById(id)

    suspend fun addGoal(goal: GoalEntity): Long =
        goalDao.insertGoal(goal)

    suspend fun updateGoal(goal: GoalEntity) =
        goalDao.updateGoal(goal)

    suspend fun archiveGoal(id: Int) =
        goalDao.archiveGoal(id)

    suspend fun restoreGoal(id: Int) {
        val goal = goalDao.getGoalById(id) ?: return
        goalDao.updateGoal(goal.copy(isArchived = false))
    }

    suspend fun updateProgress(
        goalId: Int,
        progress: Double
    ) {
        goalDao.updateGoalProgress(goalId, progress)

        val goal = goalDao.getGoalById(goalId) ?: return
        val target = goal.targetValue ?: return

        if (target > 0 && progress >= target) {
            goalDao.completeGoal(goalId)
        }
    }

    suspend fun deleteGoal(goal: GoalEntity) =
        goalDao.deleteGoal(goal)

    // Goal milestones

    fun getMilestones(
        goalId: Int
    ): Flow<List<GoalMilestoneEntity>> =
        milestoneDao.getMilestonesForGoal(goalId)

    suspend fun addMilestone(
        milestone: GoalMilestoneEntity
    ): Long =
        milestoneDao.insertMilestone(milestone)

    suspend fun updateMilestone(
        milestone: GoalMilestoneEntity
    ) =
        milestoneDao.updateMilestone(milestone)

    suspend fun deleteMilestone(
        milestone: GoalMilestoneEntity
    ) =
        milestoneDao.deleteMilestone(milestone)

    suspend fun completeMilestone(
        milestoneId: Int
    ) =
        milestoneDao.completeMilestone(milestoneId)

    suspend fun getCompletedMilestoneCount(
        goalId: Int
    ): Int =
        milestoneDao.getCompletedMilestoneCount(goalId)

    // Habit-goal linking

    fun getHabitIdsForGoal(
        goalId: Int
    ): Flow<List<Int>> =
        habitGoalDao.getHabitIdsForGoal(goalId)

    fun getGoalIdsForHabit(
        habitId: Int
    ): Flow<List<Int>> =
        habitGoalDao.getGoalIdsForHabit(habitId)

    suspend fun linkHabitToGoal(
        habitId: Int,
        goalId: Int
    ): Long =
        habitGoalDao.linkHabitToGoal(
            HabitGoalCrossRef(
                habitId = habitId,
                goalId = goalId
            )
        )

    suspend fun unlinkHabitFromGoal(
        habitId: Int,
        goalId: Int
    ) =
        habitGoalDao.unlinkHabitFromGoal(habitId, goalId)

    suspend fun isHabitLinkedToGoal(
        habitId: Int,
        goalId: Int
    ): Boolean =
        habitGoalDao.isHabitLinkedToGoal(habitId, goalId)
}
