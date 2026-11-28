
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val description: String = "",

    val category: String,

    // DAILY, SELECTED_DAYS, WEEKLY_TARGET
    val frequency: String,

    // Example: 4 means four days per week
    val targetPerWeek: Int = 7,

    // Comma-separated weekdays: MONDAY,WEDNESDAY,FRIDAY
    val selectedDays: String = "",

    // Optional measurable target
    // Example: 2 litres, 30 minutes, 5 problems
    val targetValue: Double? = null,

    val unit: String = "",

    val startDate: Long = System.currentTimeMillis(),

    val endDate: Long? = null,

    val reminderEnabled: Boolean = false,

    val reminderTime: String = "",

    val isActive: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)
