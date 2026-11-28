
package com.example.lifeos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "habit_goal_cross_ref",
    primaryKeys = ["habitId", "goalId"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("habitId"),
        Index("goalId")
    ]
)
data class HabitGoalCrossRef(

    val habitId: Int,

    val goalId: Int,

    val linkedAt: Long = System.currentTimeMillis()
)
