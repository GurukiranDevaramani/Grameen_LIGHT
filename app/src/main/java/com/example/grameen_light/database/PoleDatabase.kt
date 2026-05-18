package com.example.grameen_light.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PoleEntity::class], version = 1, exportSchema = false)
abstract class PoleDatabase : RoomDatabase() {
    abstract fun poleDao(): PoleDao

    companion object {
        @Volatile
        private var INSTANCE: PoleDatabase? = null

        fun getDatabase(context: Context): PoleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PoleDatabase::class.java,
                    "pole_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
