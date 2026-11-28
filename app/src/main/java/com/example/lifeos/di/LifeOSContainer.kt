
package com.example.lifeos.di

import android.content.Context
import com.example.lifeos.data.local.LifeOSDatabase
import com.example.lifeos.data.repository.HabitRepository
import com.example.lifeos.data.repository.GoalRepository

class LifeOSContainer(context: Context) {

    private val database = LifeOSDatabase.getDatabase(context)

    val habitRepository by lazy {
        HabitRepository(
            habitDao = database.habitDao(),
            completionDao = database.habitCompletionDao()
        )
    }

    val goalRepository by lazy {
        GoalRepository(
            goalDao = database.goalDao(),
            milestoneDao = database.goalMilestoneDao(),
            habitGoalDao = database.habitGoalDao()
        )
    }
}
