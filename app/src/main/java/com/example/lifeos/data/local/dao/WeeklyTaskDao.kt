
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeos.data.local.entity.WeeklyTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyTask(task: WeeklyTaskEntity): Long

    @Update
    suspend fun updateWeeklyTask(task: WeeklyTaskEntity)

    @Delete
    suspend fun deleteWeeklyTask(task: WeeklyTaskEntity)

    // Tasks for a weekday, available from their creation date onward
    @Query("""
        SELECT * FROM weekly_tasks
        WHERE dayOfWeek = :dayOfWeek
        AND isActive = 1
        AND date(createdAt / 1000, 'unixepoch', 'localtime') <= :date
        ORDER BY createdAt ASC
    """)
    fun getWeeklyTasksForDate(
        dayOfWeek: Int,
        date: String
    ): Flow<List<WeeklyTaskEntity>>

    // All active weekly tasks for management
    @Query("""
        SELECT * FROM weekly_tasks
        WHERE isActive = 1
        ORDER BY dayOfWeek ASC, createdAt ASC
    """)
    fun getAllActiveWeeklyTasks(): Flow<List<WeeklyTaskEntity>>

    @Query("SELECT * FROM weekly_tasks WHERE id = :taskId LIMIT 1")
    suspend fun getWeeklyTaskById(taskId: Int): WeeklyTaskEntity?
}
