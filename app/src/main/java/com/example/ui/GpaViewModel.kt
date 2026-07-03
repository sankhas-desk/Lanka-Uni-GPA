package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SemesterStats(
    val semesterNumber: Int,
    val sgpa: Double,
    val totalCredits: Double,
    val totalPoints: Double,
    val courses: List<Course>
)

data class GpaStats(
    val cumulativeGpa: Double = 0.0,
    val totalCredits: Double = 0.0,
    val totalPoints: Double = 0.0,
    val semesterWiseStats: List<SemesterStats> = emptyList(),
    val honorsClassification: String = "No courses added yet",
    val classificationColor: Long = 0xFF7D5260
)

class GpaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GpaRepository

    init {
        val database = GpaDatabase.getDatabase(application)
        repository = GpaRepository(database.gpaDao)
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allCourses: StateFlow<List<Course>> = repository.allCourses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val gpaStatistics: StateFlow<GpaStats> = combine(allCourses, userProfile) { courses, profile ->
        val scale = profile?.gradingScale ?: "Standard"
        calculateStats(courses, scale)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GpaStats()
    )

    // Profile updates
    fun updateProfile(
        name: String,
        dpIndicator: String,
        university: String,
        degree: String,
        gradingScale: String,
        isDarkMode: Boolean? = userProfile.value?.isDarkMode
    ) {
        viewModelScope.launch {
            val updated = UserProfile(
                id = 1,
                name = name,
                dpIndicator = dpIndicator,
                university = university,
                degree = degree,
                gradingScale = gradingScale,
                isDarkMode = isDarkMode
            )
            repository.saveProfile(updated)
        }
    }

    // Helper to persist standard content URI into local app files directory
    fun saveCustomGpPhoto(uriStr: String): String? {
        val uri = android.net.Uri.parse(uriStr) ?: return null
        val context = getApplication<Application>()
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = java.io.File(context.filesDir, "profile_custom_dp.jpg")
            val outputStream = java.io.FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Course modifications
    fun addCourse(semesterNumber: Int, code: String, name: String, credits: Double, grade: String) {
        viewModelScope.launch {
            val course = Course(
                semesterNumber = semesterNumber,
                code = code,
                name = name,
                credits = credits,
                grade = grade
            )
            repository.addCourse(course)
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            repository.updateCourse(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }

    fun clearSemester(semesterNumber: Int) {
        viewModelScope.launch {
            repository.clearSemester(semesterNumber)
        }
    }

    private fun calculateStats(courses: List<Course>, gradingScale: String): GpaStats {
        if (courses.isEmpty()) return GpaStats()

        var cumulCredits = 0.0
        var cumulPoints = 0.0
        val semWiseList = mutableListOf<SemesterStats>()

        for (semNum in 1..8) {
            val semCourses = courses.filter { it.semesterNumber == semNum }
            var semCredits = 0.0
            var semPoints = 0.0

            for (c in semCourses) {
                val gp = getGradePoint(c.grade, gradingScale)
                semCredits += c.credits
                semPoints += (c.credits * gp)
            }

            val sgpa = if (semCredits > 0) semPoints / semCredits else 0.0
            semWiseList.add(
                SemesterStats(
                    semesterNumber = semNum,
                    sgpa = sgpa,
                    totalCredits = semCredits,
                    totalPoints = semPoints,
                    courses = semCourses
                )
            )

            cumulCredits += semCredits
            cumulPoints += semPoints
        }

        val cgpa = if (cumulCredits > 0) cumulPoints / cumulCredits else 0.0

        // Sri Lankan honours class standards
        val honors = when {
            cgpa >= 3.70 -> "First Class Honours (GPA ≥ 3.70)"
            cgpa >= 3.30 -> "Second Class Upper Division (3.30 – 3.69)"
            cgpa >= 3.00 -> "Second Class Lower Division (3.00 – 3.29)"
            cgpa >= 2.00 -> "Ordinary Pass (2.00 – 2.99)"
            else -> "Below Requirement / Academic Warning"
        }

        val color = when {
            cgpa >= 3.70 -> 0xFF0E5E4E // Deep Emerald Green
            cgpa >= 3.30 -> 0xFF2A8F79 // Sage teal
            cgpa >= 3.00 -> 0xFFE5A93B // Gold
            cgpa >= 2.00 -> 0xFF718280 // Neutral Grey
            else -> 0xFFC63A1D // Warning Red
        }

        return GpaStats(
            cumulativeGpa = cgpa,
            totalCredits = cumulCredits,
            totalPoints = cumulPoints,
            semesterWiseStats = semWiseList,
            honorsClassification = honors,
            classificationColor = color
        )
    }
}
