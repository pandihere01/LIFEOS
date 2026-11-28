
package com.example.lifeos.data.repository

import com.example.lifeos.data.local.entity.HabitEntity
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object HabitSchedule {

    private val zone: ZoneId
        get() = ZoneId.systemDefault()

    private fun toLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp)
            .atZone(zone)
            .toLocalDate()
    }

    fun isScheduledForDate(
        habit: HabitEntity,
        date: LocalDate
    ): Boolean {
        if (!habit.isActive) return false

        val startDate = toLocalDate(habit.startDate)

        val endDate = habit.endDate?.let {
            toLocalDate(it)
        }

        if (date.isBefore(startDate)) return false
        if (endDate != null && date.isAfter(endDate)) return false

        return when (habit.frequency) {
            "DAILY" -> true

            "SELECTED_DAYS" -> {
                val selectedDays = habit.selectedDays
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toSet()

                date.dayOfWeek.value in selectedDays
            }

            "WEEKLY_TARGET" -> habit.targetPerWeek in 1..7

            else -> false
        }
    }

    fun getWeeklyTarget(habit: HabitEntity): Int {
        return when (habit.frequency) {
            "DAILY" -> 7

            "SELECTED_DAYS" -> habit.selectedDays
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .distinct()
                .size

            "WEEKLY_TARGET" -> habit.targetPerWeek.coerceIn(1, 7)

            else -> 0
        }
    }

    fun getWeekStart(date: LocalDate): LocalDate {
        return date.with(DayOfWeek.MONDAY)
    }

    fun getWeekDates(date: LocalDate): List<LocalDate> {
        val monday = getWeekStart(date)
        return (0..6).map { monday.plusDays(it.toLong()) }
    }
}
