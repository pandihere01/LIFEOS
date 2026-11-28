
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goal_milestones",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class GoalMilestoneEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val goalId: Int,

    val title: String,

    val description: String = "",

    val targetValue: Double? = null,

    val isCompleted: Boolean = false,

    val completedAt: Long? = null,

    val createdAt: Long = System.currentTimeMillis()
)
