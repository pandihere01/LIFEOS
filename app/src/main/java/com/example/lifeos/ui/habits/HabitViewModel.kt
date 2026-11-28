
package com.example.lifeos.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeos.data.local.entity.HabitEntity
import com.example.lifeos.data.local.entity.HabitCompletionEntity
import com.example.lifeos.data.repository.HabitRepository
import com.example.lifeos.data.repository.HabitTrackingEngine
import com.example.lifeos.data.repository.HabitSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _habits = MutableStateFlow<List<HabitEntity>>(emptyList())
    val habits: StateFlow<List<HabitEntity>> = _habits.asStateFlow()

    private val _completionHistory =
        MutableStateFlow<Map<Int, List<HabitCompletionEntity>>>(emptyMap())

    val completionHistory: StateFlow<Map<Int, List<HabitCompletionEntity>>> =
        _completionHistory.asStateFlow()

    private val _streaks = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val streaks: StateFlow<Map<Int, Int>> = _streaks.asStateFlow()

    private val observingHabitIds = mutableSetOf<Int>()

    init {
        viewModelScope.launch {
            repository.getActiveHabits().collect { habitList ->
                _habits.value = habitList

                habitList.forEach { habit ->
                    observeCompletions(habit)
                }
            }
        }
    }

    private fun observeCompletions(habit: HabitEntity) {
        if (!observingHabitIds.add(habit.id)) return

        viewModelScope.launch {
            repository.getHabitCompletions(habit.id).collect { history ->
                _completionHistory.value =
                    _completionHistory.value + (habit.id to history)

                _streaks.value = _streaks.value + (
                        habit.id to HabitTrackingEngine.calculateCurrentStreak(
                            habit,
                            history
                        )
                        )
            }
        }
    }

    fun addHabit(habit: HabitEntity) {
        if (habit.name.isBlank()) return

        if (
            habit.frequency == "SELECTED_DAYS" &&
            habit.selectedDays.isBlank()
        ) return

        if (
            habit.frequency == "WEEKLY_TARGET" &&
            habit.targetPerWeek !in 1..7
        ) return

        if (habit.targetValue != null && habit.targetValue <= 0) return

        viewModelScope.launch {
            repository.addHabit(habit)
        }
    }

    fun updateHabit(habit: HabitEntity) {
        if (habit.name.isBlank()) return

        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun archiveHabit(habitId: Int) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)

            observingHabitIds.remove(habit.id)
            _completionHistory.value -= habit.id
            _streaks.value -= habit.id
        }
    }

    fun recordCompletion(
        habitId: Int,
        date: LocalDate = LocalDate.now(),
        value: Double = 0.0,
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.recordCompletion(
                habitId = habitId,
                date = date.toString(),
                completedValue = value,
                note = note
            )
        }
    }

    fun undoCompletion(
        habitId: Int,
        date: LocalDate = LocalDate.now()
    ) {
        viewModelScope.launch {
            repository.undoCompletion(
                habitId = habitId,
                date = date.toString()
            )
        }
    }

    fun getStreak(habitId: Int): Int =
        _streaks.value[habitId] ?: 0

    fun isCompletedOnDate(
        habitId: Int,
        date: LocalDate
    ): Boolean {
        val habit = _habits.value.find { it.id == habitId }
            ?: return false

        val completion = _completionHistory.value[habitId]
            ?.find { it.completionDate == date.toString() }
            ?: return false

        return HabitTrackingEngine.isCompletionSuccessful(
            habit,
            completion
        )
    }

    fun getWeeklyCompletionCount(
        habitId: Int,
        date: LocalDate = LocalDate.now()
    ): Int {
        val habit = _habits.value.find { it.id == habitId }
            ?: return 0

        val weekDates = HabitSchedule.getWeekDates(date)

        return weekDates.count { day ->
            HabitSchedule.isScheduledForDate(habit, day) &&
                    isCompletedOnDate(habitId, day)
        }
    }
}
