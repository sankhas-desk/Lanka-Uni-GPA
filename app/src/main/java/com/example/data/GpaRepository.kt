package com.example.data

import kotlinx.coroutines.flow.Flow

class GpaRepository(private val gpaDao: GpaDao) {
    val userProfile: Flow<UserProfile?> = gpaDao.getUserProfile()
    val allCourses: Flow<List<Course>> = gpaDao.getAllCourses()

    fun getCoursesForSemester(semNum: Int): Flow<List<Course>> = gpaDao.getCoursesForSemester(semNum)

    suspend fun saveProfile(profile: UserProfile) {
        gpaDao.insertOrUpdateProfile(profile)
    }

    suspend fun addCourse(course: Course) {
        gpaDao.insertCourse(course)
    }

    suspend fun updateCourse(course: Course) {
        gpaDao.updateCourse(course)
    }

    suspend fun deleteCourse(course: Course) {
        gpaDao.deleteCourse(course)
    }

    suspend fun clearSemester(semNum: Int) {
        gpaDao.clearSemesterCourses(semNum)
    }
}
