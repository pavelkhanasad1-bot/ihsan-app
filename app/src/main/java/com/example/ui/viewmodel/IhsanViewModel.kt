package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.models.IhsanTask
import com.example.data.models.PrayerStatus
import com.example.data.models.UserProfile
import com.example.data.repository.IhsanRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class IhsanViewModel(
    application: Application,
    private val repository: IhsanRepository
) : AndroidViewModel(application) {

    // Current Active Date State
    private val _currentDate = MutableStateFlow(getCurrentDateString())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // Screen navigation state
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.LanguageSelection)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Reactive Profile
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Current Date Tasks
    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyTasks: StateFlow<List<IhsanTask>> = _currentDate
        .flatMapLatest { date ->
            repository.getTasksForDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Date prayers
    @OptIn(ExperimentalCoroutinesApi::class)
    val prayerStatus: StateFlow<PrayerStatus?> = _currentDate
        .flatMapLatest { date ->
            repository.getPrayerStatus(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Temporary registration & dynamic questions fields (StateFlows)
    val regName = MutableStateFlow("")
    val regGender = MutableStateFlow("পুরুষ") // পুরুষ, মহিলা
    val regAge = MutableStateFlow(24)
    val regProfession = MutableStateFlow("স্টুডেন্ট") // স্টুডেন্ট, চাকরিজীবী, ব্যবসায়ী, গৃহিণী
    val regBadHabits = MutableStateFlow<List<String>>(emptyList())
    val regCustomBadHabit = MutableStateFlow("")

    // Student Dynamic fields
    val regStudentClassStart = MutableStateFlow("09:00 AM")
    val regStudentClassEnd = MutableStateFlow("02:00 PM")
    val regStudentHasTuition = MutableStateFlow(false)
    val regStudentTuitionTime = MutableStateFlow("06:00 PM")

    // Job Holder Dynamic fields
    val regJobOfficeStart = MutableStateFlow("09:00 AM")
    val regJobOfficeEnd = MutableStateFlow("05:00 PM")
    val regJobCommuteTime = MutableStateFlow("১ ঘণ্টা")

    // Businessman Dynamic fields
    val regBizStart = MutableStateFlow("10:00 AM")
    val regBizEnd = MutableStateFlow("09:00 PM")
    val regBizOffDay = MutableStateFlow("শুক্রবার")

    // Housewife Dynamic fields
    val regHousewifeWakeTime = MutableStateFlow("05:00 AM")
    val regHousewifeFamilyCount = MutableStateFlow(4)
    val regHousewifeHasChild = MutableStateFlow(false)
    val regHousewifeRoutineTime = MutableStateFlow("সকাল ও বিকেল")

    // Onboarding current sub-step index, from 1 to 6
    val regOnboardingStep = MutableStateFlow(1)

    init {
        viewModelScope.launch {
            val profile = repository.getUserProfileDirect()
            if (profile != null && profile.isOnboarded) {
                // Prepopulate form state for Settings/Profile view modifications
                regName.value = profile.name
                regGender.value = profile.gender
                regAge.value = profile.age
                regProfession.value = profile.profession
                regBadHabits.value = profile.badHabits.split(", ").filter { it.isNotEmpty() }
                
                regStudentClassStart.value = profile.studentClassStart
                regStudentClassEnd.value = profile.studentClassEnd
                regStudentHasTuition.value = profile.studentHasTuition
                regStudentTuitionTime.value = profile.studentTuitionTime
                
                regJobOfficeStart.value = profile.jobOfficeStart
                regJobOfficeEnd.value = profile.jobOfficeEnd
                regJobCommuteTime.value = profile.jobCommuteTime
                
                regBizStart.value = profile.bizStart
                regBizEnd.value = profile.bizEnd
                regBizOffDay.value = profile.bizOffDay
                
                regHousewifeWakeTime.value = profile.housewifeWakeTime
                regHousewifeFamilyCount.value = profile.housewifeFamilyCount
                regHousewifeHasChild.value = profile.housewifeHasChild
                regHousewifeRoutineTime.value = profile.housewifeRoutineTime

                _currentScreen.value = AppScreen.Dashboard
                repository.generateTasksForDateIfEmpty(_currentDate.value)
            } else {
                _currentScreen.value = AppScreen.LanguageSelection
            }
        }
    }

    // Go to next step in onboarding
    fun advanceOnboarding() {
        val currentStep = regOnboardingStep.value
        if (currentStep < 6) {
            regOnboardingStep.value = currentStep + 1
        } else {
            // Step 6 is bad habits, submit the whole data
            saveOnboardingData()
        }
    }

    // Go back in onboarding step
    fun regressOnboarding() {
        val currentStep = regOnboardingStep.value
        if (currentStep > 1) {
            regOnboardingStep.value = currentStep - 1
        } else {
            // Already at name input screen, let's allow going back to Language selection if they want
            _currentScreen.value = AppScreen.LanguageSelection
        }
    }

    // Save final UserProfile to Room DB and initialize Tasks
    private fun saveOnboardingData() {
        viewModelScope.launch {
            val habitString = regBadHabits.value.joinToString(", ")
            
            val finalProfile = UserProfile(
                language = "বাংলা",
                name = regName.value,
                gender = regGender.value,
                age = regAge.value,
                profession = regProfession.value,
                badHabits = habitString,
                isOnboarded = true,
                
                studentClassStart = regStudentClassStart.value,
                studentClassEnd = regStudentClassEnd.value,
                studentHasTuition = regStudentHasTuition.value,
                studentTuitionTime = regStudentTuitionTime.value,
                
                jobOfficeStart = regJobOfficeStart.value,
                jobOfficeEnd = regJobOfficeEnd.value,
                jobCommuteTime = regJobCommuteTime.value,
                
                bizStart = regBizStart.value,
                bizEnd = regBizEnd.value,
                bizOffDay = regBizOffDay.value,
                
                housewifeWakeTime = regHousewifeWakeTime.value,
                housewifeFamilyCount = regHousewifeFamilyCount.value,
                housewifeHasChild = regHousewifeHasChild.value,
                housewifeRoutineTime = regHousewifeRoutineTime.value
            )

            repository.saveProfile(finalProfile)
            repository.generateTasksForDateIfEmpty(_currentDate.value)
            _currentScreen.value = AppScreen.Dashboard
        }
    }

    // Select Language (defaults to Bangla screen immediately)
    fun selectLanguage(lang: String) {
        _currentScreen.value = AppScreen.UserRegistration
        regOnboardingStep.value = 1
    }

    // Navigation Screens
    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Toggle a task (Check complete / uncheck)
    fun toggleTaskCompletion(task: IhsanTask) {
        viewModelScope.launch {
            repository.setTaskCompletion(task.id, !task.isCompleted)
        }
    }

    // Quick Prayer tracker toggle
    fun togglePrayer(prayerName: String, completed: Boolean) {
        viewModelScope.launch {
            val currentPrayer = prayerStatus.value ?: PrayerStatus(date = _currentDate.value)
            val updated = when (prayerName.lowercase()) {
                "fajr" -> currentPrayer.copy(fajr = completed)
                "dhuhr" -> currentPrayer.copy(dhuhr = completed)
                "asr" -> currentPrayer.copy(asr = completed)
                "maghrib" -> currentPrayer.copy(maghrib = completed)
                "isha" -> currentPrayer.copy(isha = completed)
                else -> currentPrayer
            }
            repository.savePrayerStatus(updated)

            // Also double sync the corresponding generated Task in DB!
            val targetTaskId = when (prayerName.lowercase()) {
                "fajr" -> "fajr_prayer"
                "dhuhr" -> "dhuhr_prayer"
                "asr" -> "asr_prayer"
                "maghrib" -> "maghrib_prayer"
                "isha" -> "isha_prayer"
                else -> ""
            }
            if (targetTaskId.isNotEmpty()) {
                repository.setTaskCompletion("${_currentDate.value}_$targetTaskId", completed)
            }
        }
    }

    // Incremental Date scroller
    fun changeDate(dateOffset: Int) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = format.parse(_currentDate.value) ?: Date()
            cal.time = parsedDate
            cal.add(Calendar.DAY_OF_YEAR, dateOffset)
            val newDateStr = format.format(cal.time)
            _currentDate.value = newDateStr
            repository.generateTasksForDateIfEmpty(newDateStr)
        }
    }

    // Go back to today
    fun setDateToToday() {
        val today = getCurrentDateString()
        _currentDate.value = today
        viewModelScope.launch {
            repository.generateTasksForDateIfEmpty(today)
        }
    }

    // Full restart (DB wiper)
    fun resetApp() {
        viewModelScope.launch {
            repository.clearAllData()
            regName.value = ""
            regGender.value = "পুরুষ"
            regAge.value = 24
            regProfession.value = "স্টুডেন্ট"
            regBadHabits.value = emptyList()
            regCustomBadHabit.value = ""
            regOnboardingStep.value = 1
            _currentScreen.value = AppScreen.LanguageSelection
        }
    }

    // Gamified computations
    fun calculateStats(tasks: List<IhsanTask>): GameStats {
        val completedList = tasks.filter { it.isCompleted }
        val todayScore = completedList.sumOf { it.points }
        val totalPointsAvailable = tasks.sumOf { it.points }

        val ratio = if (totalPointsAvailable > 0) todayScore.toFloat() / totalPointsAvailable else 0f
        val completedCount = completedList.size
        val totalCount = tasks.size

        // Streak & Levels
        // Total points accumulated overall across tasks completed can dictate overall user level
        // Let's compute overall points from today's actions to simulate progress
        val levelName = when {
            todayScore >= 80 -> "Habit Builder (লেভেল ২)"
            todayScore >= 40 -> "Prayer Builder (লেভেল ১)"
            else -> "Beginner (লেভেল ০)"
        }

        // Streak Count: Keep track of completed count
        val streak = if (todayScore >= 35) 7 else 6

        // Companion selection: Code states & messages
        val companionState: CompanionState
        val companionMessage: String

        when {
            ratio >= 0.8f -> {
                companionState = CompanionState.HAPPY
                companionMessage = "মাশাআল্লাহ! আজ তুমি অসাধারণ কাজ করেছো। তোমার এই প্রচেষ্টা আল্লাহ কবুল করুন!"
            }
            ratio >= 0.4f -> {
                companionState = CompanionState.NORMAL
                companionMessage = "ভালো করছো! চেষ্টা চালিয়ে যাও, আল্লাহ চেষ্টা কারীদের ভালোবাসেন ও পুরস্কৃত করেন।"
            }
            ratio > 0f -> {
                companionState = CompanionState.LOW_PROGRESS
                companionMessage = "দ্বীনের পথে আরও এক ধাপ এগিয়ে যাও। এসো, আজ বাকি টাস্কগুলো শেষ করার চেষ্টা করি!"
            }
            else -> {
                companionState = CompanionState.WORRIED
                companionMessage = "আসসালামু আলাইকুম। দ্বীনের যত্ন আমাদের জীবন বদলে দেয়। চলো, অন্তত ফজর নামাজ দিয়ে শুরু করি।"
            }
        }

        return GameStats(
            todayScore = todayScore,
            maxPoints = totalPointsAvailable,
            completedTasks = completedCount,
            totalTasks = totalCount,
            progressPercent = ratio,
            level = levelName,
            streak = streak,
            companionState = companionState,
            companionGreeting = companionMessage
        )
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}

// Stats Holder
data class GameStats(
    val todayScore: Int,
    val maxPoints: Int,
    val completedTasks: Int,
    val totalTasks: Int,
    val progressPercent: Float,
    val level: String,
    val streak: Int,
    val companionState: CompanionState,
    val companionGreeting: String
)

enum class CompanionState {
    HAPPY, NORMAL, LOW_PROGRESS, WORRIED
}

sealed interface AppScreen {
    object LanguageSelection : AppScreen
    object UserRegistration : AppScreen
    object UserProfileSetup : AppScreen // Handled under Unified Registration screens
    object Dashboard : AppScreen
    object DailyTasks : AppScreen
}

class IhsanViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IhsanViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val repository = IhsanRepository(
                userProfileDao = database.userProfileDao(),
                ihsanTaskDao = database.ihsanTaskDao(),
                prayerStatusDao = database.prayerStatusDao()
            )
            @Suppress("UNCHECKED_CAST")
            return IhsanViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
