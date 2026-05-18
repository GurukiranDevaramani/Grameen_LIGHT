package com.example.grameen_light.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PoleDao {
    @Query("SELECT * FROM poles")
    fun getAllPoles(): Flow<List<PoleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoles(poles: List<PoleEntity>)

    @Query("DELETE FROM poles")
    suspend fun deleteAll()
}
