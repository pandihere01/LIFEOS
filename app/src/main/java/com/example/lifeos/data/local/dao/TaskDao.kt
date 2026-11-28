
package com.example.lifeos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeos.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE dueDate = :date
        ORDER BY isCompleted ASC, createdAt DESC
    """)
    fun getTasksByDate(date: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE dueDate = :date AND isCompleted = 0
        ORDER BY createdAt ASC
    """)
    suspend fun getPendingTasksForDate(date: String): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate = :date")
    fun getTaskCountByDate(date: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM tasks
        WHERE dueDate = :date AND isCompleted = 1
    """)
    fun getCompletedTaskCountByDate(date: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM tasks
        WHERE dueDate = :date AND isCompleted = 0
    """)
    fun getPendingTaskCountByDate(date: String): Flow<Int>

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
