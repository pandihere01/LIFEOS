
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.entity.HabitCompletionEntity
import com.example.lifeos.data.local.entity.HabitEntity
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

object HabitTrackingEngine {

    private val weekFields = WeekFields.of(Locale.getDefault())

    fun isCompletionSuccessful(
        habit: HabitEntity,
        completion: HabitCompletionEntity
    ): Boolean {
        val target = habit.targetValue ?: return true
        return completion.completedValue >= target
    }

    fun getSuccessfulDates(
        habit: HabitEntity,
        completions: List<HabitCompletionEntity>
    ): Set<LocalDate> {
        return completions.mapNotNull { completion ->
            val date = runCatching {
                LocalDate.parse(completion.completionDate)
            }.getOrNull()

            if (
                date != null &&
                isCompletionSuccessful(habit, completion)
            ) {
                date
            } else {
                null
            }
        }.toSet()
    }

    fun calculateCurrentStreak(
        habit: HabitEntity,
        completions: List<HabitCompletionEntity>,
        today: LocalDate = LocalDate.now()
    ): Int {
        val successfulDates = getSuccessfulDates(habit, completions)

        if (successfulDates.isEmpty()) return 0

        return when (habit.frequency) {
            "WEEKLY_TARGET" -> calculateWeeklyStreak(
                habit,
                successfulDates,
                today
            )

            "DAILY", "SELECTED_DAYS" -> calculateDailyStreak(
                habit,
                successfulDates,
                today
            )

            else -> 0
        }
    }

    private fun calculateDailyStreak(
        habit: HabitEntity,
        successfulDates: Set<LocalDate>,
        today: LocalDate
    ): Int {
        var date = today

        // An unfinished habit today does not break its streak
        // until the day has ended.
        if (
            HabitSchedule.isScheduledForDate(habit, date) &&
            date !in successfulDates
        ) {
            date = date.minusDays(1)
        }

        var streak = 0

        while (!date.isBefore(
                LocalDate.ofInstant(
                    java.time.Instant.ofEpochMilli(habit.startDate),
                    java.time.ZoneId.systemDefault()
                )
            )
        ) {
            if (HabitSchedule.isScheduledForDate(habit, date)) {
                if (date in successfulDates) {
                    streak++
                } else {
                    break
                }
            }

            date = date.minusDays(1)
        }

        return streak
    }

    private fun calculateWeeklyStreak(
        habit: HabitEntity,
        successfulDates: Set<LocalDate>,
        today: LocalDate
    ): Int {
        val required = habit.targetPerWeek.coerceIn(1, 7)

        val completedPerWeek = successfulDates.groupingBy {
            it.get(weekFields.weekOfWeekBasedYear()) to
                    it.get(weekFields.weekBasedYear())
        }.eachCount()

        var weekDate = today

        val currentWeekKey = weekKey(weekDate)

        // If this week's target is not reached yet,
        // retain the previous completed week's streak.
        if ((completedPerWeek[currentWeekKey] ?: 0) < required) {
            weekDate = weekDate.minusWeeks(1)
        }

        var streak = 0

        while (true) {
            val key = weekKey(weekDate)

            if ((completedPerWeek[key] ?: 0) >= required) {
                streak++
                weekDate = weekDate.minusWeeks(1)
            } else {
                break
            }
        }

        return streak
    }

    private fun weekKey(date: LocalDate): Pair<Int, Int> {
        return date.get(weekFields.weekOfWeekBasedYear()) to
                date.get(weekFields.weekBasedYear())
    }
}
