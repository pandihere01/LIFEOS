
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lifeos.data.local.entity.WeeklyTaskCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyTaskCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCompletion(
        completion: WeeklyTaskCompletionEntity
    )

    @Query("""
        SELECT * FROM weekly_task_completions
        WHERE completionDate = :date
    """)
    fun getCompletionsForDate(
        date: String
    ): Flow<List<WeeklyTaskCompletionEntity>>

    @Query("""
        SELECT * FROM weekly_task_completions
        WHERE weeklyTaskId = :taskId
        AND completionDate = :date
        LIMIT 1
    """)
    suspend fun getCompletion(
        taskId: Int,
        date: String
    ): WeeklyTaskCompletionEntity?

    @Query("""
        DELETE FROM weekly_task_completions
        WHERE weeklyTaskId = :taskId
    """)
    suspend fun deleteCompletionsForTask(taskId: Int)
}
