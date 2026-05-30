package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.IhsanTaskDao
import com.example.data.dao.PrayerStatusDao
import com.example.data.dao.UserProfileDao
import com.example.data.models.IhsanTask
import com.example.data.models.PrayerStatus
import com.example.data.models.UserProfile

@Database(
    entities = [UserProfile::class, IhsanTask::class, PrayerStatus::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun ihsanTaskDao(): IhsanTaskDao
    abstract fun prayerStatusDao(): PrayerStatusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ihsan_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
