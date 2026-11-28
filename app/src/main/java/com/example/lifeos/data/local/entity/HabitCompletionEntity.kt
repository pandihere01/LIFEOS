
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completions",
    indices = [
        Index(
            value = ["habitId", "completionDate"],
            unique = true
        )
    ]
)
data class HabitCompletionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val habitId: Int,

    // Local date in yyyy-MM-dd format
    val completionDate: String,

    // Actual measurable progress, if applicable
    val completedValue: Double = 0.0,

    val note: String = "",

    val createdAt: Long = System.currentTimeMillis()
)
