
package com.example.lifeos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.lifeos.data.local.dao.TaskDao
import com.example.lifeos.data.local.dao.CalendarEventDao
import com.example.lifeos.data.local.dao.ReminderDao
import com.example.lifeos.data.local.dao.HabitDao
import com.example.lifeos.data.local.dao.HabitCompletionDao
import com.example.lifeos.data.local.dao.GoalDao

import com.example.lifeos.data.local.entity.TaskEntity
import com.example.lifeos.data.local.entity.CalendarEventEntity
import com.example.lifeos.data.local.entity.ReminderEntity
import com.example.lifeos.data.local.entity.HabitEntity
import com.example.lifeos.data.local.entity.HabitCompletionEntity
import com.example.lifeos.data.local.entity.GoalEntity
import com.example.lifeos.data.local.entity.GoalMilestoneEntity
import com.example.lifeos.data.local.entity.HabitGoalCrossRef

import com.example.lifeos.data.local.dao.GoalMilestoneDao
import com.example.lifeos.data.local.dao.HabitGoalDao

import com.example.lifeos.data.local.dao.WeeklyTaskDao
import com.example.lifeos.data.local.dao.WeeklyTaskCompletionDao

import com.example.lifeos.data.local.entity.WeeklyTaskEntity
import com.example.lifeos.data.local.entity.WeeklyTaskCompletionEntity

@Database(
    entities = [
        TaskEntity::class,
        CalendarEventEntity::class,
        ReminderEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        GoalEntity::class,
        GoalMilestoneEntity::class,
        HabitGoalCrossRef::class,
        WeeklyTaskEntity::class,
        WeeklyTaskCompletionEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class LifeOSDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun calendarEventDao(): CalendarEventDao

    abstract fun reminderDao(): ReminderDao

    abstract fun habitDao(): HabitDao

    abstract fun habitCompletionDao(): HabitCompletionDao

    abstract fun goalDao(): GoalDao

    abstract fun goalMilestoneDao(): GoalMilestoneDao

    abstract fun habitGoalDao(): HabitGoalDao

    abstract fun weeklyTaskDao(): WeeklyTaskDao

    abstract fun weeklyTaskCompletionDao(): WeeklyTaskCompletionDao

    companion object {

        @Volatile
        private var INSTANCE: LifeOSDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS calendar_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        startTime INTEGER NOT NULL,
                        endTime INTEGER NOT NULL,
                        location TEXT NOT NULL,
                        isAllDay INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS reminders (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        reminderTime INTEGER NOT NULL,
                        priority TEXT NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        isRecurring INTEGER NOT NULL,
                        recurrenceType TEXT NOT NULL,
                        isNotificationEnabled INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                // Habits
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS habits (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        category TEXT NOT NULL,
                        frequency TEXT NOT NULL,
                        targetPerWeek INTEGER NOT NULL,
                        selectedDays TEXT NOT NULL,
                        targetValue REAL,
                        unit TEXT NOT NULL,
                        startDate INTEGER NOT NULL,
                        endDate INTEGER,
                        reminderEnabled INTEGER NOT NULL,
                        reminderTime TEXT NOT NULL,
                        isActive INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // Habit completion history
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS habit_completions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        habitId INTEGER NOT NULL,
                        completionDate TEXT NOT NULL,
                        completedValue REAL NOT NULL,
                        note TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_habit_completions_habitId_completionDate
                    ON habit_completions (habitId, completionDate)
                """.trimIndent())

                // Goals
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS goals (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        category TEXT NOT NULL,
                        goalType TEXT NOT NULL,
                        targetValue REAL,
                        currentValue REAL NOT NULL,
                        unit TEXT NOT NULL,
                        startDate INTEGER NOT NULL,
                        deadline INTEGER,
                        isCompleted INTEGER NOT NULL,
                        isArchived INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // Goal milestones
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS goal_milestones (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        goalId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        targetValue REAL,
                        isCompleted INTEGER NOT NULL,
                        completedAt INTEGER,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY(goalId) REFERENCES goals(id)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS
                    index_goal_milestones_goalId
                    ON goal_milestones (goalId)
                """.trimIndent())

                // Habit-goal relationship
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS habit_goal_cross_ref (
                        habitId INTEGER NOT NULL,
                        goalId INTEGER NOT NULL,
                        linkedAt INTEGER NOT NULL,
                        PRIMARY KEY(habitId, goalId),
                        FOREIGN KEY(habitId) REFERENCES habits(id)
                        ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(goalId) REFERENCES goals(id)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS
                    index_habit_goal_cross_ref_habitId
                    ON habit_goal_cross_ref (habitId)
                """.trimIndent())

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS
                    index_habit_goal_cross_ref_goalId
                    ON habit_goal_cross_ref (goalId)
                """.trimIndent())
            }
        }


        private val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
            CREATE TABLE tasks_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                isCompleted INTEGER NOT NULL,
                createdAt INTEGER NOT NULL,
                dueDate TEXT NOT NULL
            )
        """.trimIndent())

                db.execSQL("""
            INSERT INTO tasks_new (id, title, isCompleted, createdAt, dueDate)
            SELECT
                id,
                title,
                isCompleted,
                createdAt,
                date(createdAt / 1000, 'unixepoch', 'localtime')
            FROM tasks
        """.trimIndent())

                db.execSQL("DROP TABLE tasks")

                db.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS weekly_tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                dayOfWeek INTEGER NOT NULL,
                isActive INTEGER NOT NULL,
                createdAt INTEGER NOT NULL
            )
        """.trimIndent())

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS weekly_task_completions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                weeklyTaskId INTEGER NOT NULL,
                completionDate TEXT NOT NULL,
                isCompleted INTEGER NOT NULL,
                completedAt INTEGER,
                FOREIGN KEY(weeklyTaskId)
                    REFERENCES weekly_tasks(id)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
        """.trimIndent())

                db.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_weekly_task_completions_weeklyTaskId_completionDate
            ON weekly_task_completions (weeklyTaskId, completionDate)
        """.trimIndent())
            }
        }


        fun getDatabase(context: Context): LifeOSDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOSDatabase::class.java,
                    "lifeos_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5
                    )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
