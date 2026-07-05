package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Vehicle
import com.example.data.model.OilChangeLog

@Database(
    entities = [Vehicle::class, OilChangeLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oilReminderDao(): OilReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oil_reminder_database"
                )
                .fallbackToDestructiveMigration() // For development safety
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
