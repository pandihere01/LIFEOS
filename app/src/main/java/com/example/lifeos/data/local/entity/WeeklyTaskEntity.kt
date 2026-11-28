
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_tasks")
data class WeeklyTaskEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    // ISO weekday: Monday = 1, Sunday = 7
    val dayOfWeek: Int,

    val isActive: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)
