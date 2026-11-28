
package com.example.lifeos.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeos.data.local.entity.TaskEntity
import com.example.lifeos.data.local.entity.WeeklyTaskEntity
import com.example.lifeos.data.local.entity.WeeklyTaskCompletionEntity
import com.example.lifeos.data.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    // Currently selected calendar date
    private val _selectedDate =
        MutableStateFlow(LocalDate.now().toString())

    val selectedDate: StateFlow<String> =
        _selectedDate.asStateFlow()

    // One-time tasks
    private val _tasks =
        MutableStateFlow<List<TaskEntity>>(emptyList())

    val tasks: StateFlow<List<TaskEntity>> =
        _tasks.asStateFlow()

    // Weekly recurring task templates for selected weekday
    private val _weeklyTasks =
        MutableStateFlow<List<WeeklyTaskEntity>>(emptyList())

    val weeklyTasks: StateFlow<List<WeeklyTaskEntity>> =
        _weeklyTasks.asStateFlow()

    // Completion history for selected date
    private val _weeklyCompletions =
        MutableStateFlow<List<WeeklyTaskCompletionEntity>>(emptyList())

    val weeklyCompletions: StateFlow<List<WeeklyTaskCompletionEntity>> =
        _weeklyCompletions.asStateFlow()

    // All active weekly tasks, useful for weekly management
    private val _allWeeklyTasks =
        MutableStateFlow<List<WeeklyTaskEntity>>(emptyList())

    val allWeeklyTasks: StateFlow<List<WeeklyTaskEntity>> =
        _allWeeklyTasks.asStateFlow()

    // Daily task statistics (one-time tasks only for now)
    val completedTaskCount: Int
        get() = _tasks.value.count { it.isCompleted }

    val pendingTaskCount: Int
        get() = _tasks.value.count { !it.isCompleted }

    init {
        observeSelectedDateTasks()
        observeWeeklyTasks()
        observeWeeklyCompletions()
        observeAllWeeklyTasks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSelectedDateTasks() {
        viewModelScope.launch {
            _selectedDate
                .flatMapLatest { date ->
                    repository.getTasksByDate(date)
                }
                .collectLatest { taskList ->
                    _tasks.value = taskList
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeWeeklyTasks() {
        viewModelScope.launch {
            _selectedDate
                .flatMapLatest { date ->
                    val dayOfWeek = LocalDate.parse(date).dayOfWeek.value

                    repository.getWeeklyTasksForDate(
                        dayOfWeek = dayOfWeek,
                        date = date
                    )
                }
                .collectLatest { taskList ->
                    _weeklyTasks.value = taskList
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeWeeklyCompletions() {
        viewModelScope.launch {
            _selectedDate
                .flatMapLatest { date ->
                    repository.getWeeklyCompletionsForDate(date)
                }
                .collectLatest { completions ->
                    _weeklyCompletions.value = completions
                }
        }
    }

    private fun observeAllWeeklyTasks() {
        viewModelScope.launch {
            repository.getAllActiveWeeklyTasks()
                .collectLatest { taskList ->
                    _allWeeklyTasks.value = taskList
                }
        }
    }

    // Select another date
    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    // Return to today's tasks
    fun selectToday() {
        _selectedDate.value = LocalDate.now().toString()
    }

    // Add a one-time task
    fun addTask(title: String) {
        val trimmedTitle = title.trim()

        if (trimmedTitle.isNotEmpty()) {
            viewModelScope.launch {
                repository.addTask(
                    title = trimmedTitle,
                    dueDate = _selectedDate.value
                )
            }
        }
    }

    // Update one-time task completion
    fun updateTask(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(isCompleted = isCompleted)
            )
        }
    }

    // Delete one-time task
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Add a recurring task to a weekday (Monday = 1, Sunday = 7)
    fun addWeeklyTask(title: String, dayOfWeek: Int) {
        val trimmedTitle = title.trim()

        if (trimmedTitle.isNotEmpty() && dayOfWeek in 1..7) {
            viewModelScope.launch {
                repository.addWeeklyTask(
                    title = trimmedTitle,
                    dayOfWeek = dayOfWeek
                )
            }
        }
    }

    // Update recurring task (title, weekday, active status)
    fun updateWeeklyTask(task: WeeklyTaskEntity) {
        viewModelScope.launch {
            repository.updateWeeklyTask(task)
        }
    }

    // Delete recurring task and its completion history
    fun deleteWeeklyTask(task: WeeklyTaskEntity) {
        viewModelScope.launch {
            repository.deleteWeeklyTask(task)
        }
    }

    // Toggle completion for the currently selected date only
    fun updateWeeklyTaskCompletion(
        task: WeeklyTaskEntity,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            repository.setWeeklyTaskCompletion(
                taskId = task.id,
                date = _selectedDate.value,
                isCompleted = isCompleted
            )
        }
    }

    // Check whether a recurring task is completed on selected date
    fun isWeeklyTaskCompleted(taskId: Int): Boolean {
        return _weeklyCompletions.value.any {
            it.weeklyTaskId == taskId && it.isCompleted
        }
    }
}
