
package com.example.lifeos.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeos.data.local.entity.TaskEntity
import com.example.lifeos.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun addTask(title: String) {
        val trimmedTitle = title.trim()

        if (trimmedTitle.isNotEmpty()) {
            viewModelScope.launch {
                repository.addTask(trimmedTitle)
            }
        }
    }

    fun updateTask(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(isCompleted = isCompleted)
            )
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
