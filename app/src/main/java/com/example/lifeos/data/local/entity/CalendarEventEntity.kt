
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String = "",

    val startTime: Long,

    val endTime: Long,

    val location: String = "",

    val isAllDay: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)
