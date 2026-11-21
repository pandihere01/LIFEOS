
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.dao.TaskDao
import com.example.lifeos.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    suspend fun addTask(title: String) {
        taskDao.insertTask(
            TaskEntity(title = title)
        )
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }
}
