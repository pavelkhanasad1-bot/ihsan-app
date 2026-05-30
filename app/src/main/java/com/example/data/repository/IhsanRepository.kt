package com.example.data.repository

import com.example.data.dao.IhsanTaskDao
import com.example.data.dao.PrayerStatusDao
import com.example.data.dao.UserProfileDao
import com.example.data.models.IhsanTask
import com.example.data.models.PrayerStatus
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

class IhsanRepository(
    private val userProfileDao: UserProfileDao,
    private val ihsanTaskDao: IhsanTaskDao,
    private val prayerStatusDao: PrayerStatusDao
) {
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun getUserProfileDirect(): UserProfile? = userProfileDao.getUserProfileDirect()

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.saveUserProfile(profile)
    }

    suspend fun clearAllData() {
        userProfileDao.clearProfile()
        ihsanTaskDao.clearTasks()
        prayerStatusDao.clearPrayers()
    }

    fun getTasksForDate(date: String): Flow<List<IhsanTask>> = ihsanTaskDao.getTasksForDate(date)

    fun getPrayerStatus(date: String): Flow<PrayerStatus?> = prayerStatusDao.getPrayerStatusFlow(date)

    suspend fun setTaskCompletion(taskId: String, completed: Boolean) {
        ihsanTaskDao.setTaskCompletion(taskId, completed)
    }

    suspend fun savePrayerStatus(prayerStatus: PrayerStatus) {
        prayerStatusDao.savePrayerStatus(prayerStatus)
    }

    suspend fun generateTasksForDateIfEmpty(date: String) {
        val existingTasks = ihsanTaskDao.getTasksForDateDirect(date)
        if (existingTasks.isEmpty()) {
            val profile = userProfileDao.getUserProfileDirect()
            
            val sampleTasks = mutableListOf<IhsanTask>()
            
            // 1. Wake Up Task
            val wakeTime = if (profile?.profession == "গৃহিণী") profile.housewifeWakeTime else "05:00 AM"
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_wake_up",
                    taskId = "wake_up",
                    title = "ঘুম থেকে ওঠা",
                    time = wakeTime,
                    description = "সকালে আল্লাহর নামে ঘুম থেকে ওঠা এবং জাগ্রত হওয়ার দোয়া ও কৃতজ্ঞতা প্রকাশ করা।",
                    category = "Routine",
                    date = date,
                    points = 10
                )
            )

            // 2. Fajr Prayer (+20 points specified!)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_fajr_prayer",
                    taskId = "fajr_prayer",
                    title = "ফজর নামাজ",
                    time = "05:15 AM",
                    description = "দ্বীনের পথে অবিচল থাকার জন্য ফজর নামাজ জামাতে আদায় করার চেষ্টা করুন। (+২০ পয়েন্ট)",
                    category = "Prayer",
                    date = date,
                    points = 20
                )
            )

            // 3. Morning Adhkar (+5 points specified!)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_morning_adhkar",
                    taskId = "morning_adhkar",
                    title = "সকাল দোয়া ও আদকার",
                    time = "05:30 AM",
                    description = "সকালের হিফাজত ও বরকতের জন্য মাসনুন দোয়া এবং যিকিরসমূহ পাঠ করুন। (+৫ পয়েন্ট)",
                    category = "Adhkar",
                    date = date,
                    points = 5
                )
            )

            // 4. Quran Reading (+10 points specified!)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_quran_reading",
                    taskId = "quran_reading",
                    title = "কুরআন তেলাওয়াত ও তাদাব্বুর",
                    time = "06:00 AM",
                    description = "অর্থসহ অন্তত একটি রুকু বা কয়েকটি আয়াত মনোযোগ দিয়ে তেলাওয়াত করুন। (+১০ পয়েন্ট)",
                    category = "Quran",
                    date = date,
                    points = 10
                )
            )

            // 5. Profession specific dynamic tasks
            if (profile != null) {
                when (profile.profession) {
                    "স্টুডেন্ট" -> {
                        sampleTasks.add(
                            IhsanTask(
                                id = "${date}_student_class",
                                taskId = "student_class",
                                title = "ক্লাস ও পড়াশোনা",
                                time = profile.studentClassStart,
                                description = "ক্লাস শুরু ${profile.studentClassStart} থেকে শেষ ${profile.studentClassEnd} পর্যন্ত একাগ্রতার সাথে অধ্যয়ন করা।",
                                category = "Productivity",
                                date = date,
                                points = 10
                            )
                        )
                        if (profile.studentHasTuition) {
                            sampleTasks.add(
                                IhsanTask(
                                    id = "${date}_student_tuition",
                                    taskId = "student_tuition",
                                    title = "টিউশন বা প্রাইভেট",
                                    time = profile.studentTuitionTime,
                                    description = "দ্বীনদারী রক্ষা ও আমানতদারীর সাথে টিউশনিতে শিক্ষা দেওয়া।",
                                    category = "Productivity",
                                    date = date,
                                    points = 10
                                )
                            )
                        }
                    }
                    "চাকরিজীবী" -> {
                        sampleTasks.add(
                            IhsanTask(
                                id = "${date}_job_office",
                                taskId = "job_office",
                                title = "অফিসের কাজ",
                                time = profile.jobOfficeStart,
                                description = "অফিস কর্মঘণ্টা (${profile.jobOfficeStart} - ${profile.jobOfficeEnd}) আল্লাহর প্রতি ভালোবাসা ও সততায় কাটানো।",
                                category = "Productivity",
                                date = date,
                                points = 10
                            )
                        )
                        sampleTasks.add(
                            IhsanTask(
                                id = "${date}_job_commute",
                                taskId = "job_commute",
                                title = "অফিস যাতায়াত যিকির",
                                time = "08:00 AM",
                                description = "যাতায়াতের সময়টা (${profile.jobCommuteTime}) হেডফোনে গান না শুনে আল্লাহর জিকির ও লেকচার শুনে সময় কাজে লাগানো।",
                                category = "Productivity",
                                date = date,
                                points = 5
                            )
                        )
                    }
                    "ব্যবসায়ী" -> {
                        sampleTasks.add(
                            IhsanTask(
                                id = "${date}_biz_shop",
                                taskId = "biz_shop",
                                title = "ব্যবসা পরিচালনা (হালাল ও সত্যবাদিতা)",
                                time = profile.bizStart,
                                description = "ব্যবসা পরিচালনার লক্ষ্য হালাল উপার্জন। ব্যবসা সময়: ${profile.bizStart} - ${profile.bizEnd}।",
                                category = "Productivity",
                                date = date,
                                points = 10
                            )
                        )
                        sampleTasks.add(
                            IhsanTask(
                                id = "${date}_biz_rest",
                                taskId = "biz_rest",
                                title = "সাপ্তাহিক ছুটি ও দ্বীনচর্চা",
                                time = "১০:০০ AM",
                                description = "ছুটির দিন (${profile.bizOffDay}) পরিবারের সাথে সময় কাটান ও ইসলামি দ্বীন চর্চা করুন।",
                                category = "Routine",
                                date = date,
                                points = 10
                            )
                        )
                    }
                    "গৃহিণী" -> {
                        sampleTasks.add(
                            IhsanTask(
                                id = "${date}_housewife_duty",
                                taskId = "housewife_duty",
                                title = "পরিবারের যত্ন ও গৃহস্থালি কাজ",
                                time = "০৮:০০ AM",
                                description = "পরিবারের ${profile.housewifeFamilyCount} জন সদস্যের জন্য রান্না ও যত্নের দায়িত্ব পালন করা। নিয়ত রাখা ইবাদতের।",
                                category = "Productivity",
                                date = date,
                                points = 10
                            )
                        )
                        if (profile.housewifeHasChild) {
                            sampleTasks.add(
                                IhsanTask(
                                    id = "${date}_housewife_child",
                                    taskId = "housewife_child",
                                    title = "সন্তানের লালন-পালন ও আদাব শিক্ষা",
                                    time = profile.housewifeRoutineTime,
                                    description = "সন্তানকে মাসনুন দোয়া, নৈতিকতা ও সুন্দর ইসলামিক আদব শিক্ষা দান।",
                                    category = "Productivity",
                                    date = date,
                                    points = 10
                                )
                            )
                        }
                    }
                    else -> {
                        // General productivity fallback
                        sampleTasks.add(
                            IhsanTask(
                                id = "${date}_general_productivity",
                                taskId = "general_productivity",
                                title = "কাজ / পড়াশোনা",
                                time = "08:00 AM",
                                description = "সততা ও নিষ্ঠার সাথে আপনার কাজ বা পড়ার সময়টুকু পার করুন। এহসান বজায় রাখুন।",
                                category = "Productivity",
                                date = date,
                                points = 10
                            )
                        )
                    }
                }
            } else {
                // Default fallback work task
                sampleTasks.add(
                    IhsanTask(
                        id = "${date}_work_study",
                        taskId = "work_study",
                        title = "কাজ / পড়াশোনা",
                        time = "08:00 AM",
                        description = "সততা ও নিষ্ঠার সাথে আপনার কাজ বা পড়ার সময়টুকু পার করুন। এহসান বজায় রাখুন।",
                        category = "Productivity",
                        date = date,
                        points = 10
                    )
                )
            }

            // 6. Sadqah Task (+15 points requested!)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_sadqah_action",
                    taskId = "sadqah_action",
                    title = "দিনের সদকা সমাধান",
                    time = "11:00 AM",
                    description = "যে কোনো ভালো কাজ সদকা সমতুল্য। অন্যকে সাহায্য করুন অথবা সামান্য অনুদান দিন। (+১৫ পয়েন্ট)",
                    category = "Productivity",
                    date = date,
                    points = 15
                )
            )

            // 7. Dhuhr Prayer (13:15)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_dhuhr_prayer",
                    taskId = "dhuhr_prayer",
                    title = "যোহর নামাজ",
                    time = "01:15 PM",
                    description = "যোহরের ফরজ ৮ রাকাত সহ প্রয়োজনীয় সুন্নাহ সালাত সময়মতো আদায় করুন।",
                    category = "Prayer",
                    date = date,
                    points = 10
                )
            )

            // 8. Asr Prayer (16:30)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_asr_prayer",
                    taskId = "asr_prayer",
                    title = "আসর নামাজ",
                    time = "04:30 PM",
                    description = "বিকেলের ব্যস্ততা সত্ত্বেও আসর নামাজ যথা সময়ে আদায় করে আল্লাহর সান্নিধ্য লাভ করুন।",
                    category = "Prayer",
                    date = date,
                    points = 10
                )
            )

            // 9. Maghrib Prayer (18:30)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_maghrib_prayer",
                    taskId = "maghrib_prayer",
                    title = "মাগরিব নামাজ",
                    time = "18:30 PM",
                    description = "সূর্যাস্তের পর বিলম্ব না করে মাগরিবের সালাত সমাপ্ত করুন।",
                    category = "Prayer",
                    date = date,
                    points = 10
                )
            )

            // 10. Isha Prayer (21:00)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_isha_prayer",
                    taskId = "isha_prayer",
                    title = "এশা নামাজ",
                    time = "21:00 PM",
                    description = "এশার সালাত এবং বিতর নামাজ আদায় করে দিনটি সুন্দরভাবে সমাপ্ত করুন।",
                    category = "Prayer",
                    date = date,
                    points = 10
                )
            )

            // 11. Sleep Dua (22:00) (+5 points specified!)
            sampleTasks.add(
                IhsanTask(
                    id = "${date}_sleep_dua",
                    taskId = "sleep_dua",
                    title = "ঘুমের দোয়া ও রাতে শয়ন দোয়া",
                    time = "22:00 PM",
                    description = "রাসুলুল্লাহ (সাঃ)-এর সুন্নাত অনুযায়ী ঘুমের দোয়া ও আয়াতুল কুরসি পাঠ করা। (+৫ পয়েন্ট)",
                    category = "Routine",
                    date = date,
                    points = 5
                )
            )

            ihsanTaskDao.insertTasks(sampleTasks)
        }

        // Initialize prayer status object if not existing
        val existingPrayer = prayerStatusDao.getPrayerStatusDirect(date)
        if (existingPrayer == null) {
            prayerStatusDao.savePrayerStatus(PrayerStatus(date = date))
        }
    }
}
