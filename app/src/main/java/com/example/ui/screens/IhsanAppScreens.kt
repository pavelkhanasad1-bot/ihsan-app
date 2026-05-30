@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.IhsanTask
import com.example.data.models.UserProfile
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CompanionState
import com.example.ui.viewmodel.IhsanViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Navigation Coordinator
@Composable
fun IhsanAppNavigation(viewModel: IhsanViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 350),
            label = "screen_root_fade"
        ) { screen ->
            when (screen) {
                AppScreen.LanguageSelection -> LanguageSelectionScreen(viewModel)
                AppScreen.UserRegistration -> OnboardingWizardScreen(viewModel)
                AppScreen.UserProfileSetup -> OnboardingWizardScreen(viewModel) // integrated cleanly
                AppScreen.Dashboard -> MainNavigationHost(viewModel, initialTab = 0)
                AppScreen.DailyTasks -> MainNavigationHost(viewModel, initialTab = 1)
            }
        }
    }
}

// 1. Language Selection Screen (Required Intro)
@Composable
fun LanguageSelectionScreen(viewModel: IhsanViewModel) {
    var selectedLang by remember { mutableStateOf("বাংলা") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Theme
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                IslamicPatternLogo()
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "ইহসান অ্যাপ",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Better Deen, Better Life",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }

            // Language Selector Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ভাষা নির্বাচন করুন / Select Language",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val languages = listOf(
                        Triple("বাংলা (Bangla Only)", "দ্বীনদার জীবন গঠন করার সহজ সমাধান", "বাংলা"),
                        Triple("English (Bangla Preferred)", "Islamic Personal Life Companion app", "English")
                    )

                    languages.forEach { (langTitle, langSubtitle, tag) ->
                        val isSelected = selectedLang == tag
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else Color.Transparent
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedLang = tag }
                                .padding(16.dp)
                                .testTag("lang_${tag}_option"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedLang = tag },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = langTitle,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = langSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Continue Button (Always moves forward to clean Bangla MVP)
            Button(
                onClick = { viewModel.selectLanguage(selectedLang) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("language_continue_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "অ্যাপ শুরু করুন",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Forward link",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// 2. Comprehensive Onboarding Flow (Fully Bangla only)
@Composable
fun OnboardingWizardScreen(viewModel: IhsanViewModel) {
    val step by viewModel.regOnboardingStep.collectAsState()
    val name by viewModel.regName.collectAsState()
    val gender by viewModel.regGender.collectAsState()
    val age by viewModel.regAge.collectAsState()
    val profession by viewModel.regProfession.collectAsState()
    val badHabits by viewModel.regBadHabits.collectAsState()
    val customHabitInput by viewModel.regCustomBadHabit.collectAsState()

    // Dynamic Fields variables
    val studentClassStart by viewModel.regStudentClassStart.collectAsState()
    val studentClassEnd by viewModel.regStudentClassEnd.collectAsState()
    val studentHasTuition by viewModel.regStudentHasTuition.collectAsState()
    val studentTuitionTime by viewModel.regStudentTuitionTime.collectAsState()

    val jobOfficeStart by viewModel.regJobOfficeStart.collectAsState()
    val jobOfficeEnd by viewModel.regJobOfficeEnd.collectAsState()
    val jobCommuteTime by viewModel.regJobCommuteTime.collectAsState()

    val bizStart by viewModel.regBizStart.collectAsState()
    val bizEnd by viewModel.regBizEnd.collectAsState()
    val bizOffDay by viewModel.regBizOffDay.collectAsState()

    val housewifeWakeTime by viewModel.regHousewifeWakeTime.collectAsState()
    val housewifeFamilyCount by viewModel.regHousewifeFamilyCount.collectAsState()
    val housewifeHasChild by viewModel.regHousewifeHasChild.collectAsState()
    val housewifeRoutineTime by viewModel.regHousewifeRoutineTime.collectAsState()

    val totalSteps = 6

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with Back button and progress step bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { viewModel.regressOnboarding() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), CircleShape)
                            .testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "უკან",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "নতুন প্রোফাইল তৈরি (ধাপ $step/$totalSteps)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.width(40.dp)) // match balance weights
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Indicators
                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }

            // Step Body with animations
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    1 -> ScreenNameStep(name, onNameChange = { viewModel.regName.value = it })
                    2 -> ScreenGenderStep(gender, onGenderChange = { viewModel.regGender.value = it })
                    3 -> ScreenAgeStep(age, onAgeChange = { viewModel.regAge.value = it })
                    4 -> ScreenProfessionStep(profession, onProfessionChange = { viewModel.regProfession.value = it })
                    5 -> ScreenDynamicQuestionsStep(
                        profession = profession,
                        studentClassStart = studentClassStart,
                        studentClassEnd = studentClassEnd,
                        studentHasTuition = studentHasTuition,
                        studentTuitionTime = studentTuitionTime,
                        onStudentClassStart = { viewModel.regStudentClassStart.value = it },
                        onStudentClassEnd = { viewModel.regStudentClassEnd.value = it },
                        onStudentHasTuition = { viewModel.regStudentHasTuition.value = it },
                        onStudentTuitionTime = { viewModel.regStudentTuitionTime.value = it },
                        
                        jobOfficeStart = jobOfficeStart,
                        jobOfficeEnd = jobOfficeEnd,
                        jobCommuteTime = jobCommuteTime,
                        onJobOfficeStart = { viewModel.regJobOfficeStart.value = it },
                        onJobOfficeEnd = { viewModel.regJobOfficeEnd.value = it },
                        onJobCommuteTime = { viewModel.regJobCommuteTime.value = it },
                        
                        bizStart = bizStart,
                        bizEnd = bizEnd,
                        bizOffDay = bizOffDay,
                        onBizStart = { viewModel.regBizStart.value = it },
                        onBizEnd = { viewModel.regBizEnd.value = it },
                        onBizOffDay = { viewModel.regBizOffDay.value = it },
                        
                        housewifeWakeTime = housewifeWakeTime,
                        housewifeFamilyCount = housewifeFamilyCount,
                        housewifeHasChild = housewifeHasChild,
                        housewifeRoutineTime = housewifeRoutineTime,
                        onHousewifeWakeTime = { viewModel.regHousewifeWakeTime.value = it },
                        onHousewifeFamilyCount = { viewModel.regHousewifeFamilyCount.value = it },
                        onHousewifeHasChild = { viewModel.regHousewifeHasChild.value = it },
                        onHousewifeRoutineTime = { viewModel.regHousewifeRoutineTime.value = it }
                    )
                    6 -> ScreenBadHabitsStep(
                        badHabits = badHabits,
                        onHabitToggle = { habit ->
                            val currentList = badHabits.toMutableList()
                            if (currentList.contains(habit)) currentList.remove(habit)
                            else currentList.add(habit)
                            viewModel.regBadHabits.value = currentList
                        },
                        customInput = customHabitInput,
                        onCustomInputChange = { viewModel.regCustomBadHabit.value = it },
                        onAddCustomHabit = {
                            if (customHabitInput.trim().isNotEmpty()) {
                                val currentList = badHabits.toMutableList()
                                if (!currentList.contains(customHabitInput.trim())) {
                                    currentList.add(customHabitInput.trim())
                                }
                                viewModel.regBadHabits.value = currentList
                                viewModel.regCustomBadHabit.value = ""
                            }
                        }
                    )
                }
            }

            // Next / Confirm button
            Button(
                onClick = { viewModel.advanceOnboarding() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_continue_button"),
                enabled = when (step) {
                    1 -> name.trim().length >= 2
                    3 -> age in 5..120
                    else -> true
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Text(
                    text = if (step == totalSteps) "প্রোফাইল সম্পন্ন করুন এবং ড্যাশবোর্ড দেখুন" else "পরবর্তী ধাপ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

// Screen Wizard Step 1: Name Input
@Composable
fun ScreenNameStep(name: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IslamicPatternMiniLogo()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "আপনার শুভ নাম কি?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "ইহসান অ্যাপে আপনাকে সুন্দর সম্বোধন করতে এটি প্রয়োজন।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("আপনার পূর্ণ নাম নাম লিখুন") },
            placeholder = { Text("যেমন: তাওহীদ ইসলাম") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_name_input"),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Name Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            singleLine = true
        )
    }
}

// Screen Wizard Step 2: Gender Selection
@Composable
fun ScreenGenderStep(gender: String, onGenderChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "আপনার লিঙ্গ নির্বাচন করুন",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "এটি আমাদের সালাত ও দ্বীনি দিকনির্দেশনা এবং মাসয়ালা সমন্বয় করতে সহায়তা করবে।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        val genders = listOf("পুরুষ", "মহিলা")
        genders.forEach { item ->
            val isSelected = gender == item
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onGenderChange(item) }
                    .testTag("reg_gender_${if (item == "পুরুষ") "male" else "female"}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item == "পুরুষ") Icons.Default.Male else Icons.Default.Female,
                                contentDescription = item,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onGenderChange(item) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

// Screen Wizard Step 3: Age selection
@Composable
fun ScreenAgeStep(age: Int, onAgeChange: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "আপনার বয়স কত?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "বিভিন্ন বয়সের অনুপাতে সুন্নাহ ভিত্তিক উদ্দীপনা সাজাতে এটি সহায়ক।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$age",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "বছর",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Slider(
            value = age.toFloat(),
            onValueChange = { onAgeChange(it.toInt()) },
            valueRange = 8f..90f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("reg_age_slider"),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "৮ বছর", style = MaterialTheme.typography.bodySmall)
            Text(text = "৫০ বছর", style = MaterialTheme.typography.bodySmall)
            Text(text = "৯০ বছর", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Screen Wizard Step 4: Profession
@Composable
fun ScreenProfessionStep(profession: String, onProfessionChange: (String) -> Unit) {
    val professions = listOf("স্টুডেন্ট", "চাকরিজীবী", "ব্যবসায়ী", "গৃহিণী")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "আপনার পেশা কি?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "পেশাগত রুটিন অনুযায়ী আপনার প্রতিদিনের কাজের ইসলামিক সমাধান দেওয়া হবে।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        professions.forEach { item ->
            val isSelected = profession == item
            val icon = when (item) {
                "স্টুডেন্ট" -> Icons.Default.School
                "চাকরিজীবী" -> Icons.Default.Work
                "ব্যবসায়ী" -> Icons.Default.BusinessCenter
                else -> Icons.Default.Home
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onProfessionChange(item) }
                    .testTag("profession_${if (item == "স্টুডেন্ট") "student" else if (item == "চাকরিজীবী") "job" else if (item == "ব্যবসায়ী") "biz" else "housewife"}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

// Screen Wizard Step 5: Dynamic Questions based on active selection (HUGELY CRITICAL)
@Composable
fun ScreenDynamicQuestionsStep(
    profession: String,
    
    // Student
    studentClassStart: String,
    studentClassEnd: String,
    studentHasTuition: Boolean,
    studentTuitionTime: String,
    onStudentClassStart: (String) -> Unit,
    onStudentClassEnd: (String) -> Unit,
    onStudentHasTuition: (Boolean) -> Unit,
    onStudentTuitionTime: (String) -> Unit,
    
    // Job Holder
    jobOfficeStart: String,
    jobOfficeEnd: String,
    jobCommuteTime: String,
    onJobOfficeStart: (String) -> Unit,
    onJobOfficeEnd: (String) -> Unit,
    onJobCommuteTime: (String) -> Unit,
    
    // Biz
    bizStart: String,
    bizEnd: String,
    bizOffDay: String,
    onBizStart: (String) -> Unit,
    onBizEnd: (String) -> Unit,
    onBizOffDay: (String) -> Unit,
    
    // Housewife
    housewifeWakeTime: String,
    housewifeFamilyCount: Int,
    housewifeHasChild: Boolean,
    housewifeRoutineTime: String,
    onHousewifeWakeTime: (String) -> Unit,
    onHousewifeFamilyCount: (Int) -> Unit,
    onHousewifeHasChild: (Boolean) -> Unit,
    onHousewifeRoutineTime: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "রুটিন কাস্টমাইজেশন",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "আপনার পেশা ($profession) অনুযায়ী অল্প কিছু প্রয়োজনীয় প্রশ্নের উত্তর দিন।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (profession) {
            "স্টুডেন্ট" -> {
                // Class Start Time
                OutlinedTextField(
                    value = studentClassStart,
                    onValueChange = onStudentClassStart,
                    label = { Text("ক্লাস শুরুর সময়") },
                    placeholder = { Text("যেমন: 09:00 AM") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Class End Time
                OutlinedTextField(
                    value = studentClassEnd,
                    onValueChange = onStudentClassEnd,
                    label = { Text("ক্লাস শেষের সময়") },
                    placeholder = { Text("যেমন: 02:00 PM") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Does tuition?
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "আপনি কি টিউশনি বা প্রাইভেট করান?", fontWeight = FontWeight.Bold)
                            Switch(
                                checked = studentHasTuition,
                                onCheckedChange = onStudentHasTuition,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        if (studentHasTuition) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = studentTuitionTime,
                                onValueChange = onStudentTuitionTime,
                                label = { Text("টিউশন করার সময়") },
                                placeholder = { Text("যেমন: ০৬:০০ PM") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
            "চাকরিজীবী" -> {
                // Office Start Time
                OutlinedTextField(
                    value = jobOfficeStart,
                    onValueChange = onJobOfficeStart,
                    label = { Text("অফিস শুরু সময়") },
                    placeholder = { Text("যেমন: 09:00 AM") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Office End Time
                OutlinedTextField(
                    value = jobOfficeEnd,
                    onValueChange = onJobOfficeEnd,
                    label = { Text("অফিস শেষ সময়") },
                    placeholder = { Text("যেমন: 05:00 PM") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Commute duration
                OutlinedTextField(
                    value = jobCommuteTime,
                    onValueChange = onJobCommuteTime,
                    label = { Text("অফিস যাতায়াত সময় (যা জিকিরের জন্য বরাদ্দ)") },
                    placeholder = { Text("যেমন: ১ ঘণ্টা ৩০ মিনিট") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            "ব্যবসায়ী" -> {
                // Business Start Time
                OutlinedTextField(
                    value = bizStart,
                    onValueChange = onBizStart,
                    label = { Text("ব্যবসা শুরু সময়") },
                    placeholder = { Text("যেমন: 10:00 AM") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Business End Time
                OutlinedTextField(
                    value = bizEnd,
                    onValueChange = onBizEnd,
                    label = { Text("ব্যবসা শেষ সময়") },
                    placeholder = { Text("যেমন: 09:00 PM") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Off day
                OutlinedTextField(
                    value = bizOffDay,
                    onValueChange = onBizOffDay,
                    label = { Text("সাপ্তাহিক ছুটি দিন") },
                    placeholder = { Text("যেমন: শুক্রবার") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            "গৃহিণী" -> {
                // Wake up time
                OutlinedTextField(
                    value = housewifeWakeTime,
                    onValueChange = onHousewifeWakeTime,
                    label = { Text("ঘুম থেকে ওঠার সময়") },
                    placeholder = { Text("যেমন: 05:00 AM") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Family count
                OutlinedTextField(
                    value = if (housewifeFamilyCount > 0) housewifeFamilyCount.toString() else "",
                    onValueChange = { valInt -> onHousewifeFamilyCount(valInt.toIntOrNull() ?: 1) },
                    label = { Text("পরিবারের সদস্য সংখ্যা") },
                    placeholder = { Text("যেমন: ৪") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Has child?
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "আপনার কি সন্তান আছে?", fontWeight = FontWeight.Bold)
                            Switch(
                                checked = housewifeHasChild,
                                onCheckedChange = onHousewifeHasChild,
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                            )
                        }

                        if (housewifeHasChild) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = housewifeRoutineTime,
                                onValueChange = onHousewifeRoutineTime,
                                label = { Text("সন্তানকে পড়ানো/দ্বীন শেখানোর সময়") },
                                placeholder = { Text("যেমন: সকাল ও বিকেল") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Screen Wizard Step 6: Bad habits checklist
@Composable
fun ScreenBadHabitsStep(
    badHabits: List<String>,
    onHabitToggle: (String) -> Unit,
    customInput: String,
    onCustomInputChange: (String) -> Unit,
    onAddCustomHabit: () -> Unit
) {
    val predefinedHabits = listOf(
        "ফজর মিস করা",
        "অতিরিক্ত মোবাইল ব্যবহার",
        "গান শোনা",
        "ধূমপান",
        "গীবত করা",
        "সময় নষ্ট করা",
        "অশালীন কন্টেন্ট দেখা"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "আপনি কোন কোন অভ্যাস পরিবর্তন করতে চান?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "তযকিয়াহ (চরিত্রের উন্নয়ন) ও রিকভারি প্ল্যান তৈরি করতে সাহায্য করতে এগুলো চিহ্নিত করুন। কোনো বিচার করা হবে না।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Grid of checkable values
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            predefinedHabits.forEach { habit ->
                val isSelected = badHabits.contains(habit)
                FilterChip(
                    selected = isSelected,
                    onClick = { onHabitToggle(habit) },
                    label = { Text(habit) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "নির্বাচিত",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("habit_pill_${habit.replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom Habit Added Input Row
        OutlinedTextField(
            value = customInput,
            onValueChange = onCustomInputChange,
            label = { Text("অন্যান্য খারাপ অভ্যাস (যদি থাকে)") },
            placeholder = { Text("যেমন: খিটখিটে মেজাজ, দেরিতে ঘুমানো...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("custom_bad_habit_input"),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = onAddCustomHabit) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "যোগ করুন",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            singleLine = true
        )

        // Selected Custom list chips
        val manualItems = badHabits.filter { !predefinedHabits.contains(it) }
        if (manualItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                manualItems.forEach { valCustom ->
                    InputChip(
                        selected = true,
                        onClick = { onHabitToggle(valCustom) },
                        label = { Text(valCustom) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "মুছুন",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

// 3. Navigation Host for main MVP application
@Composable
fun MainNavigationHost(viewModel: IhsanViewModel, initialTab: Int) {
    var activeTab by remember { mutableStateOf(initialTab) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Dashboard
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "ড্যাশবোর্ড"
                        )
                    },
                    label = { Text("হোম") },
                    modifier = Modifier.testTag("nav_dashboard_tab")
                )

                // Tab 1: Tasks
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = "দৈনন্দিন কর্মতালিকা"
                        )
                    },
                    label = { Text("টাস্কস") },
                    modifier = Modifier.testTag("nav_tasks_tab")
                )

                // Tab 2: Habits
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "অভ্যাস নিরাময়"
                        )
                    },
                    label = { Text("পুনরুদ্ধার") },
                    modifier = Modifier.testTag("nav_habits_tab")
                )

                // Tab 3: Duas
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "দোয়া ও সুন্নাহ"
                        )
                    },
                    label = { Text("দোয়া") },
                    modifier = Modifier.testTag("nav_duas_tab")
                )

                // Tab 4: Profile & Achievements
                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "প্রোফাইল ও সেটিংস"
                        )
                    },
                    label = { Text("প্রোফাইল") },
                    modifier = Modifier.testTag("nav_profile_tab")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (activeTab) {
                0 -> DashboardScreen(viewModel, onNavigateToTasks = { activeTab = 1 })
                1 -> DailyTasksScreen(viewModel)
                2 -> HabitRecoveryScreen(viewModel)
                3 -> DuasAndSunnahScreen(viewModel)
                4 -> ProfileAndSettingsScreen(viewModel)
            }
        }
    }
}

// 4. Dashboard (MAIN SCREEN in Bangla)
@Composable
fun DashboardScreen(viewModel: IhsanViewModel, onNavigateToTasks: () -> Unit) {
    val currentDate by viewModel.currentDate.collectAsState()
    val tasks by viewModel.dailyTasks.collectAsState()
    val prayers by viewModel.prayerStatus.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val stats = viewModel.calculateStats(tasks)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Upper Decorative G gilded header with profile and logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D5E4C), // Gilded forest emerald primary
                            Color(0xFF1E3A32)
                        )
                    )
                    drawRect(brush)
                }
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "আসসালামু আলাইকুম,",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = profile?.name ?: "আল্লাহর বান্দা",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Logo Icon accent of App
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = "মস্ক আইকন",
                            tint = Color(0xFFF7D13E) // golden yellow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tagline badge
                Row(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "পয়েন্ট",
                        tint = Color(0xFFF7D13E),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Better Deen, Better Life. ইহসান অ্যাপ।",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Floating Date Card Scroller
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-14).dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.changeDate(-1) },
                    modifier = Modifier.testTag("date_prev_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "পূর্ববর্তী দিন",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.setDateToToday() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "আজকের তারিখ",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Format Date in Bangla!
                    Text(
                        text = formatDateBanglaBeautiful(currentDate),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                IconButton(
                    onClick = { viewModel.changeDate(1) },
                    modifier = Modifier.testTag("date_next_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "পরবর্তী দিন",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Summary scorecard section (Gamification: Level, Streaks, Daily score ring)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text(
                text = " আজকের দ্বীনি অগ্রগতির স্কোরকার্ড",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Double Arc Circle Score Ring
                    ScoreChartRing(
                        score = stats.todayScore,
                        progress = stats.progressPercent,
                        modifier = Modifier.size(110.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Level Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "লেভেল",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stats.level,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        // Streak Indicator flame
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFFFF7E6), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "স্ট্রিক",
                                tint = Color(0xFFFF9900),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${stats.streak} দিন একটানা (স্ট্রিক)",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFCC7A00)
                                )
                            )
                        }

                        // Completed tasks count text badge
                        Text(
                            text = "টাস্ক সম্পন্ন: ${stats.completedTasks}/${stats.totalTasks}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Text(
                            text = "আজকের পয়েন্ট: +${stats.todayScore}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }

            // 5. HUZUR COMPANION UI (NO CHATBOT) on Dashboard
            Text(
                text = " হুজুর কো-পাইলট (ইসলামিক গাইড)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Representation
                    HuzurAvatar(state = stats.companionState)

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "হুজুরCompanion ",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = when (stats.companionState) {
                                        CompanionState.HAPPY -> "😊 খুশি"
                                        CompanionState.NORMAL -> "🙂 স্বাভাবিক"
                                        CompanionState.LOW_PROGRESS -> "😐 মনযোগী"
                                        CompanionState.WORRIED -> "😟 চিন্তিত"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stats.companionGreeting,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // 6. Prayer Tracker strip
            Text(
                text = " নামাজ ট্র্যাকার (সালাত আদায়)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "আজকের ৫ ওয়াক্ত মহান আল্লাহর সালাত পূর্ণ করুন:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val prayersList = listOf(
                            Triple("ফজর", prayers?.fajr ?: false, "fajr"),
                            Triple("যোহর", prayers?.dhuhr ?: false, "dhuhr"),
                            Triple("আসর", prayers?.asr ?: false, "asr"),
                            Triple("মাগরিব", prayers?.maghrib ?: false, "maghrib"),
                            Triple("এশা", prayers?.isha ?: false, "isha")
                        )

                        prayersList.forEach { (prayerName, isCompleted, key) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.togglePrayer(key, !isCompleted) }
                                    .testTag("prayer_${key}_cell")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (key == "fajr" || key == "isha") Icons.Default.NightlightRound else Icons.Default.WbSunny,
                                        contentDescription = prayerName,
                                        tint = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = prayerName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = if (key == "fajr") "+২০" else "+১০",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Quick display of today's tasks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = " আজকের কর্মতালিকা",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                TextButton(onClick = onNavigateToTasks) {
                    Text(text = "সব দেখুন", fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "সব দেখুন")
                }
            }

            // Quick Tasks loop list (Show only non-completed tasks or basic tasks)
            val incompleteTasks = tasks.filter { !it.isCompleted }
            if (incompleteTasks.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "আলহামদুলিল্লাহ! আপনার আজকের সব দ্বীনি কর্ম ও সালাত সম্পন্ন হয়েছে! 🎉",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                incompleteTasks.take(3).forEach { task ->
                    TaskRowItem(task, onComplete = { viewModel.toggleTaskCompletion(task) })
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// 4.1 Score Ring Graphic
@Composable
fun ScoreChartRing(score: Int, progress: Float, modifier: Modifier = Modifier) {
    val completedAngle = progress * 360f
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val goldAccent = MaterialTheme.colorScheme.tertiary

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw background stroke
            drawCircle(color = secondaryColor, radius = size.minDimension / 2, style = Stroke(width = 10.dp.toPx()))

            // Draw primary score arc
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = completedAngle,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw gilded inner decoration circles
            drawCircle(
                color = goldAccent.copy(alpha = 0.4f),
                radius = (size.minDimension / 2) - 14.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = primaryColor,
                    fontSize = 32.sp
                )
            )
            Text(
                text = "উপার্জিত পয়েন্ট",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// 4.2 Huzur Companion Avatar Visual Design representation (Dynamic Face)
@Composable
fun HuzurAvatar(state: CompanionState) {
    val backgroundColor = when (state) {
        CompanionState.HAPPY -> Color(0xFFE6F7ED) // soft green
        CompanionState.NORMAL -> Color(0xFFEAF5F2) // soft sage
        CompanionState.LOW_PROGRESS -> Color(0xFFFFF7E6) // soft golden peach
        CompanionState.WORRIED -> Color(0xFFFFECEF) // soft pinkish rose
    }

    val iconColor = when (state) {
        CompanionState.HAPPY -> Color(0xFF1EA362)
        CompanionState.NORMAL -> Color(0xFF1E3A32)
        CompanionState.LOW_PROGRESS -> Color(0xFFCC7A00)
        CompanionState.WORRIED -> Color(0xFFD61A3C)
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(3.dp, iconColor.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (state) {
                    CompanionState.HAPPY -> "😊"
                    CompanionState.NORMAL -> "🙂"
                    CompanionState.LOW_PROGRESS -> "😐"
                    CompanionState.WORRIED -> "😟"
                },
                fontSize = 32.sp
            )
        }
    }
}

// Task row component
@Composable
fun TaskRowItem(task: IhsanTask, onComplete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("task_${task.taskId}_item"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = "সময়",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.time,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Badge Points
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+${task.points} পয়েন্ট",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Completed / Checkbox toggle button
            Button(
                onClick = onComplete,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (task.isCompleted) MaterialTheme.colorScheme.outlineVariant
                    else MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("task_complete_btn_${task.taskId}")
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "সম্পন্ন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "সম্পূর্ণ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

// 5. Daily Tasks Screen (Filtered & Complete list + ticking system)
@Composable
fun DailyTasksScreen(viewModel: IhsanViewModel) {
    val tasks by viewModel.dailyTasks.collectAsState()
    var selectedFilter by remember { mutableStateOf("চলমান") } // চলমান, সম্পন্ন, সব

    val filteredList = when (selectedFilter) {
        "চলমান" -> tasks.filter { !it.isCompleted }
        "সম্পন্ন" -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "দৈনন্দিন ইবাদত কর্মতালিকা",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "নিম্নের মাসনুন দোয়া, কাজ ও নামাজ ট্র্যাকার নিয়মিত সম্পন্ন করুন। 'সম্পূর্ণ' চাপলে এটি পয়েন্টে জমা হবে।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filter chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("চলমান", "সম্পন্ন", "সব").forEach { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    modifier = Modifier.testTag("task_filter_${filter}"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Tasks list
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🙌",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "এই ফিল্টারে কোনো টাস্ক নেই!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = filteredList,
                    key = { it.id }
                ) { task ->
                    TaskRowItem(task, onComplete = { viewModel.toggleTaskCompletion(task) })
                }
            }
        }
    }
}

// 6. Habit Recovery Screen (Tazkiyah gradual reduction model - NO TOXIC SHAMING)
@Composable
fun HabitRecoveryScreen(viewModel: IhsanViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val habitsList = profile?.badHabits?.split(", ")?.filter { it.isNotEmpty() } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "তযকিয়াহ ও খারাপ অভ্যাস নিরাময়",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "চরিত্রের কু-অভ্যাসগুলো ধীরে ধীরে কমানোর আত্মনিয়ন্ত্রণ পরিকল্পনা। আল্লাহ চেষ্টা কারীদের ভালোবাসেন।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (habitsList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🌸", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "কোনো খারাপ অভ্যাস চিহ্নিত নেই। প্রোফাইল এডিট করে অভ্যাস যোগ করতে পারেন।",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        } else {
            habitsList.forEach { badHabit ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("recovery_habit_card_${badHabit}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFCC3300))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = badHabit,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "৭ দিনের নিরাময় কাল",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Gradual Reduction Plan (User specified)
                        Text(
                            text = "ধীরে ধীরে কমিয়ে আনার সুন্নাহ পরিকল্পনা তালিকা:",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 7-day checkboxes list
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val reductionPlan = listOf(
                                "পহেলা" to "৪ বার",
                                "২য় দিন" to "৪ বার",
                                "৩য় দিন" to "৩ বার",
                                "৪র্থ দিন" to "৩ বার",
                                "৫ম দিন" to "২ বার",
                                "৬ষ্ঠ দিন" to "১ বার",
                                "চূড়ান্ত" to "০ বার"
                            )

                            reductionPlan.forEachIndexed { idx, (dayName, limit) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = dayName,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (idx <= 2) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = limit,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Positive Substitutions alternatives (Quran, Zikr, Lectues)
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💡 বিকল্প বরকতময় সুন্নাহ প্রতিস্থাপন কাজ সমূহ:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Column(
                            modifier = Modifier.padding(top = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "📖 কুরআন তেলাওয়াত এবং অর্থসহ হৃদয় দিয়ে অনুধাবন করা।",
                                "📿 অবসর সময়ে 'তাবীহ' ও দরূদ শরীফ সহ সকাল-সন্ধ্যার জিকির করা।",
                                "🎧 কানের শান্তি ও মনোযোগ বাড়াতে ভালো রুচিশীল ইসলামিক লেকচার শোনা।"
                            ).forEach { alt ->
                                Text(
                                    text = alt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 7. Duas and Sunnah Screen (Arabi text + Bangla, bookmark/complete reading system)
@Composable
fun DuasAndSunnahScreen(viewModel: IhsanViewModel) {
    var selectedCategory by remember { mutableStateOf("সকাল ও সন্ধ্যা") }

    val morningAndEveningDuas = listOf(
        DuaItem(
            ar = "الحَمْدُ للهِ الّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            bn = "সব প্রশংসা আল্লাহর জন্য, যিনি আমাদের মৃত্যুর (ঘুমের) পর জীবিত করলেন এবং তাঁর দিকেই আমাদের ফিরে যেতে হবে।",
            title = "ঘুম থেকে ওঠার দোয়া",
            pointsAvailable = "+৫ পয়েন্ট"
        ),
        DuaItem(
            ar = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ",
            bn = "হে আল্লাহ! আপনার দয়ায় আমরা সকালে উপনীত হয়েছি, আপনার দয়ায় সন্ধ্যায় উপনীত হই, আপনার অনুগ্রহেই জীবিত থাকি এবং আপনার হুকুমেই মারা যাব আর আপনার কাছেই ফিরে যাব।",
            title = "সকালের যিকির দোয়া",
            pointsAvailable = "+৫ পয়েন্ট"
        ),
        DuaItem(
            ar = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            bn = "আল্লাহর নামে, যাঁর নামের বরকতে আসমান ও জমিনের কোনো কিছুই কোনো ক্ষতি করতে পারে না, আর তিনি সর্বশ্রোতা, মহাজ্ঞানী। (৩ বার)",
            title = "সুরক্ষা মাসনুন দোয়া",
            pointsAvailable = "+৫ পয়েন্ট"
        )
    )

    val situationDuas = listOf(
        DuaItem(
            ar = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
            bn = "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে শুরু করছি।",
            title = "খাওয়ার পূর্বের দোয়া",
            pointsAvailable = "+৫ পয়েন্ট"
        ),
        DuaItem(
            ar = "بِسْمِ اللهِ تَوَكَّلْتُ عَلَى اللهِ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللهِ",
            bn = "আল্লাহর নামে, আল্লাহর ওপর ভরসা করলাম। আল্লাহর সাহায্য ছাড়া কোনো ভালো কাজ করার বা মন্দ কাজ থেকে বাঁচার শক্তি কারো নেই।",
            title = "বাসা থেকে বের হওয়ার দোয়া",
            pointsAvailable = "+৫ পয়েন্ট"
        ),
        DuaItem(
            ar = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْخُبُثِ وَالْخَبَائِثِ",
            bn = "হে আল্লাহ! আমি আপনার নিকট অপবিত্র পুরুষ ও নারী জিনদের অনিষ্ট থেকে আশ্রয় চাচ্ছি।",
            title = "টয়লেটে প্রবেশের দোয়া",
            pointsAvailable = "+৫ পয়েন্ট"
        )
    )

    val activeDuaList = if (selectedCategory == "সকাল ও সন্ধ্যা") morningAndEveningDuas else situationDuas

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "দৈনিক দোয়া ও মাসনুন সুন্নাহ সিস্টেম",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "রাসুলুল্লাহ (সাঃ) এর নির্দেশিত সকাল, সন্ধ্যা ও পরিস্থিতির প্রয়োজনীয় আমলসমূহ।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Select tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("সকাল ও সন্ধ্যা", "পরিস্থিতি ভিত্তিক দোয়া").forEach { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("dua_category_tab_${cat}")
                )
            }
        }

        // List of scrollable card items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(activeDuaList) { item ->
                DuaCardItem(item)
            }
        }
    }
}

data class DuaItem(val ar: String, val bn: String, val title: String, val pointsAvailable: String)

@Composable
fun DuaCardItem(item: DuaItem) {
    var isReadComplete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dua_card_${item.title}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.pointsAvailable,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic text visual design
            Text(
                text = item.ar,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.5.sp,
                    lineHeight = 32.sp
                ),
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bangla Translation text
            Text(
                text = item.bn,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Complete button action inside Dua
            Button(
                onClick = { isReadComplete = !isReadComplete },
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("dua_read_complete_btn_${item.title}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isReadComplete) MaterialTheme.colorScheme.outlineVariant
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isReadComplete) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "পঠিত",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "পঠিত", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    } else {
                        Text(text = "আমল সম্পন্ন", style = MaterialTheme.typography.labelMedium.copy(color = Color.White))
                    }
                }
            }
        }
    }
}

// 8. Profile & Settings view + resetting options
@Composable
fun ProfileAndSettingsScreen(viewModel: IhsanViewModel) {
    val profile by viewModel.userProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "ইউজার প্রোফাইল ও সেটিংস",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "আপনার সংরক্ষিত ইসলামিক অনবোর্ডিং প্রোফাইল তথ্য দেখুন অথবা মেমোরি রিসেট করুন।",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // General Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "ব্যক্তিগত তথ্য",
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                ProfileDataRow("নাম", profile?.name ?: "Allah's Servant")
                ProfileDataRow("লিঙ্গ", profile?.gender ?: "পুরুষ")
                ProfileDataRow("বয়স", "${profile?.age ?: 24} বছর")
                ProfileDataRow("পেশা", profile?.profession ?: "স্টুডেন্ট")
                ProfileDataRow("অভ্যাস পরিবর্তন", profile?.badHabits ?: "কোনোটি নয়")
            }
        }

        // Dynamic Questions Info card
        if (profile != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "পেশাগত রুটিন সেটিংস",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    when (profile?.profession) {
                        "স্টুডেন্ট" -> {
                            ProfileDataRow("ক্লাস সময়", "${profile?.studentClassStart} - ${profile?.studentClassEnd}")
                            ProfileDataRow("টিউশন করান?", if (profile?.studentHasTuition == true) "হ্যাঁ" else "না")
                            if (profile?.studentHasTuition == true) {
                                ProfileDataRow("টিউশন সময়", profile?.studentTuitionTime ?: "-")
                            }
                        }
                        "চাকরিজীবী" -> {
                            ProfileDataRow("অফিস সময়", "${profile?.jobOfficeStart} - ${profile?.jobOfficeEnd}")
                            ProfileDataRow("যাতায়াত সময়", profile?.jobCommuteTime ?: "-")
                        }
                        "ব্যবসায়ী" -> {
                            ProfileDataRow("ব্যবসা সময়", "${profile?.bizStart} - ${profile?.bizEnd}")
                            ProfileDataRow("ছুটির দিন", profile?.bizOffDay ?: "-")
                        }
                        "গৃহিণী" -> {
                            ProfileDataRow("ঘুমের সময়", profile?.housewifeWakeTime ?: "-")
                            ProfileDataRow("পরিবার সদস্য", "${profile?.housewifeFamilyCount} জন")
                            ProfileDataRow("সন্তান আছে?", if (profile?.housewifeHasChild == true) "হ্যাঁ" else "না")
                            if (profile?.housewifeHasChild == true) {
                                ProfileDataRow("পড়ানোর সময়সূচি", profile?.housewifeRoutineTime ?: "-")
                            }
                        }
                    }
                }
            }
        }

        // Milestones and Achievements
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "🏆 দ্বীনি মাইলফলক অর্জন",
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                MilestonePill("৭ দিন সম্পন্ন", "Beginner (অর্জিত)", true)
                MilestonePill("১৫ দিন সম্পন্ন", "Prayer Builder (অর্জিত)", true)
                MilestonePill("৩০ দিন সম্পন্ন", "Habit Builder", false)
                MilestonePill("৬০ দিন সম্পন্ন", "Consistent Muslim", false)
                MilestonePill("৯০ দিন সম্পন্ন", "Deen Warrior", false)
            }
        }

        // Wipe / Reset Button
        Button(
            onClick = { viewModel.resetApp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("profile_logout_reset_btn"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC3300))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "রিসেট করুন", tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "অ্যাপ রিসেট করুন (নতুন প্রোফাইল তৈরি)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MilestonePill(day: String, title: String, isUnlocked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = if (isUnlocked) "উন্মুক্ত" else "লক",
                tint = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, color = if (isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = day, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// 9. Miniature Logo drawings
@Composable
fun IslamicPatternLogo() {
    Canvas(modifier = Modifier.size(90.dp)) {
        val primaryColor = Color(0xFF0D5E4C)
        val outerRadius = size.minDimension / 2.2f
        val center = Offset(size.width / 2, size.height / 2)

        // Draw overlapping geometric rotative squares
        val squares = 3
        for (i in 0 until squares) {
            val angle = (90f / squares) * i
            rotate(degrees = angle, pivot = center) {
                val side = outerRadius * 1.35f
                drawRect(
                    color = if (i == 0) primaryColor else primaryColor.copy(alpha = 0.5f - 0.1f * i),
                    topLeft = Offset(center.x - side / 2, center.y - side / 2),
                    size = Size(side, side),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Draw center star circle
        drawCircle(
            color = Color(0xFFF7D13E), // Gold
            radius = outerRadius / 3.5f
        )
    }
}

@Composable
fun IslamicPatternMiniLogo() {
    Canvas(modifier = Modifier.size(64.dp)) {
        val primaryColor = Color(0xFF0D5E4C)
        val outerRadius = size.minDimension / 2.2f
        val center = Offset(size.width / 2, size.height / 2)

        val squares = 3
        for (i in 0 until squares) {
            val angle = (90f / squares) * i
            rotate(degrees = angle, pivot = center) {
                val side = outerRadius * 1.35f
                drawRect(
                    color = if (i == 0) primaryColor else primaryColor.copy(alpha = 0.5f - 0.1f * i),
                    topLeft = Offset(center.x - side / 2, center.y - side / 2),
                    size = Size(side, side),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        drawCircle(
            color = Color(0xFFF7D13E), // Gold
            radius = outerRadius / 3.5f
        )
    }
}

// Date parser in beautiful Bangla
fun formatDateBanglaBeautiful(dateStr: String): String {
    return try {
        val formatEnglish = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatEnglish.parse(dateStr) ?: Date()
        val formatBanglaText = SimpleDateFormat("EEEE, d MMMM", Locale("bn", "BD"))
        formatBanglaText.format(date)
    } catch (e: Exception) {
        dateStr
    }
}
