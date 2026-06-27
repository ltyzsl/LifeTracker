package com.lifetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CountdownDao {
    @Query("SELECT * FROM countdowns WHERE isActive = 1 ORDER BY targetTime ASC")
    fun getActive(): Flow<List<Countdown>>

    @Query("SELECT * FROM countdowns WHERE isActive = 0 ORDER BY targetTime ASC")
    fun getFinished(): Flow<List<Countdown>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(countdown: Countdown): Long

    @Update suspend fun update(countdown: Countdown)

    @Delete suspend fun delete(countdown: Countdown)
}
