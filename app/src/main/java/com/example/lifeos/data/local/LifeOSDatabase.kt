package com.example.lifeos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lifeos.data.local.dao.TaskDao
import com.example.lifeos.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = true
)
abstract class LifeOSDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: LifeOSDatabase? = null

        fun getDatabase(context: Context): LifeOSDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOSDatabase::class.java,
                    "lifeos_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}
