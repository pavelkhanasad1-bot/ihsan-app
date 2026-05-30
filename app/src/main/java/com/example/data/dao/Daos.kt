package com.example.data.dao

import androidx.room.*
import com.example.data.models.IhsanTask
import com.example.data.models.PrayerStatus
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("DELETE FROM user_profile")
    suspend fun clearProfile()
}

@Dao
interface IhsanTaskDao {
    @Query("SELECT * FROM ihsan_tasks WHERE date = :date")
    fun getTasksForDate(date: String): Flow<List<IhsanTask>>

    @Query("SELECT * FROM ihsan_tasks WHERE date = :date")
    suspend fun getTasksForDateDirect(date: String): List<IhsanTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<IhsanTask>)

    @Update
    suspend fun updateTask(task: IhsanTask)

    @Query("UPDATE ihsan_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompletion(id: String, completed: Boolean)

    @Query("DELETE FROM ihsan_tasks")
    suspend fun clearTasks()
}

@Dao
interface PrayerStatusDao {
    @Query("SELECT * FROM prayer_status WHERE date = :date LIMIT 1")
    fun getPrayerStatusFlow(date: String): Flow<PrayerStatus?>

    @Query("SELECT * FROM prayer_status WHERE date = :date LIMIT 1")
    suspend fun getPrayerStatusDirect(date: String): PrayerStatus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrayerStatus(prayerStatus: PrayerStatus)

    @Query("DELETE FROM prayer_status")
    suspend fun clearPrayers()
}
