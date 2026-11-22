
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String = "",

    val reminderTime: Long,

    val priority: String = "MEDIUM",

    val isCompleted: Boolean = false,

    val isRecurring: Boolean = false,

    val recurrenceType: String = "NONE",

    val isNotificationEnabled: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)
