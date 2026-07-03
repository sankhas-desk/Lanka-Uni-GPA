package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class, Course::class], version = 2, exportSchema = false)
abstract class GpaDatabase : RoomDatabase() {
    abstract val gpaDao: GpaDao

    companion object {
        @Volatile
        private var INSTANCE: GpaDatabase? = null

        fun getDatabase(context: Context): GpaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GpaDatabase::class.java,
                    "gpa_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
