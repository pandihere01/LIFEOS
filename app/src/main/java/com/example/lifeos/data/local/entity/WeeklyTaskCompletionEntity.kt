
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weekly_task_completions",
    foreignKeys = [
        ForeignKey(
            entity = WeeklyTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["weeklyTaskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["weeklyTaskId", "completionDate"], unique = true)
    ]
)
data class WeeklyTaskCompletionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val weeklyTaskId: Int,

    // Date in yyyy-MM-dd format
    val completionDate: String,

    val isCompleted: Boolean = false,

    val completedAt: Long? = null
)
