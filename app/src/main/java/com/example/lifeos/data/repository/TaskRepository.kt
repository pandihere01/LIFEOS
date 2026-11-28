
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.dao.TaskDao
import com.example.lifeos.data.local.dao.WeeklyTaskDao
import com.example.lifeos.data.local.dao.WeeklyTaskCompletionDao
import com.example.lifeos.data.local.entity.TaskEntity
import com.example.lifeos.data.local.entity.WeeklyTaskEntity
import com.example.lifeos.data.local.entity.WeeklyTaskCompletionEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val weeklyTaskDao: WeeklyTaskDao,
    private val weeklyTaskCompletionDao: WeeklyTaskCompletionDao
) {

    // One-time tasks

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    fun getTasksByDate(date: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByDate(date)
    }

    fun getTaskCountByDate(date: String): Flow<Int> {
        return taskDao.getTaskCountByDate(date)
    }

    fun getCompletedTaskCountByDate(date: String): Flow<Int> {
        return taskDao.getCompletedTaskCountByDate(date)
    }

    fun getPendingTaskCountByDate(date: String): Flow<Int> {
        return taskDao.getPendingTaskCountByDate(date)
    }

    suspend fun addTask(
        title: String,
        dueDate: String = java.time.LocalDate.now().toString()
    ) {
        taskDao.insertTask(
            TaskEntity(
                title = title,
                dueDate = dueDate
            )
        )
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    // Weekly recurring task templates

    fun getWeeklyTasksForDate(
        dayOfWeek: Int,
        date: String
    ): Flow<List<WeeklyTaskEntity>> {
        return weeklyTaskDao.getWeeklyTasksForDate(dayOfWeek, date)
    }


    fun getAllActiveWeeklyTasks(): Flow<List<WeeklyTaskEntity>> {
        return weeklyTaskDao.getAllActiveWeeklyTasks()
    }

    suspend fun addWeeklyTask(
        title: String,
        dayOfWeek: Int
    ): Long {
        require(dayOfWeek in 1..7) {
            "Day of week must be between 1 (Monday) and 7 (Sunday)"
        }

        return weeklyTaskDao.insertWeeklyTask(
            WeeklyTaskEntity(
                title = title.trim(),
                dayOfWeek = dayOfWeek
            )
        )
    }

    suspend fun updateWeeklyTask(task: WeeklyTaskEntity) {
        weeklyTaskDao.updateWeeklyTask(task)
    }

    suspend fun deleteWeeklyTask(task: WeeklyTaskEntity) {
        weeklyTaskDao.deleteWeeklyTask(task)
    }

    suspend fun getWeeklyTaskById(taskId: Int): WeeklyTaskEntity? {
        return weeklyTaskDao.getWeeklyTaskById(taskId)
    }

    // Per-date completion history

    fun getWeeklyCompletionsForDate(
        date: String
    ): Flow<List<WeeklyTaskCompletionEntity>> {
        return weeklyTaskCompletionDao.getCompletionsForDate(date)
    }

    suspend fun getWeeklyCompletion(
        taskId: Int,
        date: String
    ): WeeklyTaskCompletionEntity? {
        return weeklyTaskCompletionDao.getCompletion(taskId, date)
    }

    suspend fun setWeeklyTaskCompletion(
        taskId: Int,
        date: String,
        isCompleted: Boolean
    ) {
        weeklyTaskCompletionDao.saveCompletion(
            WeeklyTaskCompletionEntity(
                weeklyTaskId = taskId,
                completionDate = date,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) {
                    System.currentTimeMillis()
                } else {
                    null
                }
            )
        )
    }

    suspend fun deleteWeeklyCompletionsForTask(taskId: Int) {
        weeklyTaskCompletionDao.deleteCompletionsForTask(taskId)
    }
}
