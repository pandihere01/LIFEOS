
package com.example.lifeos.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeos.data.local.entity.GoalEntity
import com.example.lifeos.data.local.entity.GoalMilestoneEntity
import com.example.lifeos.data.repository.GoalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals: StateFlow<List<GoalEntity>> = _goals.asStateFlow()

    private val _archivedGoals =
        MutableStateFlow<List<GoalEntity>>(emptyList())
    val archivedGoals: StateFlow<List<GoalEntity>> =
        _archivedGoals.asStateFlow()

    private val _milestones =
        MutableStateFlow<List<GoalMilestoneEntity>>(emptyList())
    val milestones: StateFlow<List<GoalMilestoneEntity>> =
        _milestones.asStateFlow()

    private val _linkedHabitIds = MutableStateFlow<List<Int>>(emptyList())
    val linkedHabitIds: StateFlow<List<Int>> =
        _linkedHabitIds.asStateFlow()

    private var milestoneJob: Job? = null
    private var habitLinkJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getActiveGoals().collect {
                _goals.value = it
            }
        }

        viewModelScope.launch {
            repository.getAllGoals().collect { allGoals ->
                _archivedGoals.value =
                    allGoals.filter { it.isArchived }
            }
        }
    }

    fun addGoal(goal: GoalEntity) {
        if (goal.title.isBlank()) return
        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun updateProgress(goalId: Int, progress: Double) {
        if (!progress.isFinite() || progress < 0.0) return
        viewModelScope.launch {
            repository.updateProgress(goalId, progress)
        }
    }

    fun archiveGoal(goalId: Int) {
        viewModelScope.launch {
            repository.archiveGoal(goalId)
        }
    }

    fun restoreGoal(goalId: Int) {
        viewModelScope.launch {
            repository.restoreGoal(goalId)
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun loadMilestones(goalId: Int) {
        milestoneJob?.cancel()
        milestoneJob = viewModelScope.launch {
            repository.getMilestones(goalId).collect {
                _milestones.value = it
            }
        }
    }

    fun addMilestone(milestone: GoalMilestoneEntity) {
        if (milestone.title.isBlank()) return
        viewModelScope.launch {
            repository.addMilestone(milestone)
        }
    }

    fun completeMilestone(milestoneId: Int) {
        viewModelScope.launch {
            repository.completeMilestone(milestoneId)
        }
    }

    fun deleteMilestone(milestone: GoalMilestoneEntity) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
        }
    }

    fun loadLinkedHabits(goalId: Int) {
        habitLinkJob?.cancel()
        habitLinkJob = viewModelScope.launch {
            repository.getHabitIdsForGoal(goalId).collect {
                _linkedHabitIds.value = it
            }
        }
    }

    fun linkHabitToGoal(habitId: Int, goalId: Int) {
        viewModelScope.launch {
            repository.linkHabitToGoal(habitId, goalId)
        }
    }

    fun unlinkHabitFromGoal(habitId: Int, goalId: Int) {
        viewModelScope.launch {
            repository.unlinkHabitFromGoal(habitId, goalId)
        }
    }

    override fun onCleared() {
        milestoneJob?.cancel()
        habitLinkJob?.cancel()
        super.onCleared()
    }
}
