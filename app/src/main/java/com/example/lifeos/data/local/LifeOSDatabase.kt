
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

import com.example.lifeos.data.local.entity.TaskEntity
import com.example.lifeos.data.local.entity.CalendarEventEntity
import com.example.lifeos.data.local.entity.ReminderEntity

@Database(
    entities = [
        TaskEntity::class,
        CalendarEventEntity::class,
        ReminderEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class LifeOSDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun calendarEventDao(): CalendarEventDao

    abstract fun reminderDao(): ReminderDao

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

        fun getDatabase(context: Context): LifeOSDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOSDatabase::class.java,
                    "lifeos_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
