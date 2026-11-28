
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    // Calendar date for which this task is planned (yyyy-MM-dd)
    val dueDate: String = java.time.Instant.ofEpochMilli(createdAt)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .toString()
)
