package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GpaDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("SELECT * FROM courses ORDER BY semesterNumber ASC, id ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE semesterNumber = :semNum ORDER BY id ASC")
    fun getCoursesForSemester(semNum: Int): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("DELETE FROM courses WHERE semesterNumber = :semNum")
    suspend fun clearSemesterCourses(semNum: Int)
}
