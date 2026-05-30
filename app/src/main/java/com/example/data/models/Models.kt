package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val language: String = "বাংলা",
    val name: String = "",
    val gender: String = "",
    val age: Int = 24,
    val profession: String = "",
    val badHabits: String = "",
    val isOnboarded: Boolean = false,
    
    // Dynamic Questions for Student
    val studentClassStart: String = "09:00 AM",
    val studentClassEnd: String = "02:00 PM",
    val studentHasTuition: Boolean = false,
    val studentTuitionTime: String = "06:00 PM",
    
    // Dynamic Questions for Job Holder (চাকরিজীবী)
    val jobOfficeStart: String = "09:00 AM",
    val jobOfficeEnd: String = "05:00 PM",
    val jobCommuteTime: String = "1 Hour",
    
    // Dynamic Questions for Businessman (ব্যবসায়ী)
    val bizStart: String = "10:00 AM",
    val bizEnd: String = "09:00 PM",
    val bizOffDay: String = "শুক্রবার", // Friday
    
    // Dynamic Questions for Housewife (গৃহিণী)
    val housewifeWakeTime: String = "05:00 AM",
    val housewifeFamilyCount: Int = 4,
    val housewifeHasChild: Boolean = false,
    val housewifeRoutineTime: String = "সকাল ও বিকেল"
)

@Entity(tableName = "ihsan_tasks")
data class IhsanTask(
    @PrimaryKey val id: String, // format: "date_taskId"
    val taskId: String,
    val title: String,
    val time: String,
    val description: String,
    val category: String, // "Routine", "Prayer", "Adhkar", "Quran", "Productivity"
    val isCompleted: Boolean = false,
    val date: String, // format: "yyyy-MM-dd"
    val points: Int = 10
)

@Entity(tableName = "prayer_status")
data class PrayerStatus(
    @PrimaryKey val date: String, // "yyyy-MM-dd"
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false
)
