
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String = "",

    val category: String,

    // SHORT_TERM or LONG_TERM
    val goalType: String,

    // Optional measurable goal
    // Example: 100 coding problems
    val targetValue: Double? = null,

    val currentValue: Double = 0.0,

    val unit: String = "",

    val startDate: Long = System.currentTimeMillis(),

    val deadline: Long? = null,

    val isCompleted: Boolean = false,

    val isArchived: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)
