package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val semesterNumber: Int, // 1 to 8 representing typical semesters
    val code: String,
    val name: String,
    val credits: Double,
    val grade: String
)

val VALID_GRADES = listOf(
    "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "E", "F"
)

fun getGradePoint(grade: String, gradingScale: String): Double {
    return when (grade.uppercase().trim()) {
        "A+" -> if (gradingScale == "Moratuwa") 4.2 else 4.0
        "A" -> 4.0
        "A-" -> 3.7
        "B+" -> 3.3
        "B" -> 3.0
        "B-" -> 2.7
        "C+" -> 2.3
        "C" -> 2.0
        "C-" -> 1.7
        "D+" -> 1.3
        "D" -> 1.0
        "E" -> 0.0
        "F" -> 0.0
        else -> 0.0
    }
}
