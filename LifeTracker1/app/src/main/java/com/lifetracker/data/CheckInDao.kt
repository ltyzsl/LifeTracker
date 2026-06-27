package com.lifetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM checkins ORDER BY checkDate DESC")
    fun getAll(): Flow<List<CheckIn>>

    @Query("SELECT DISTINCT habitName FROM checkins ORDER BY habitName ASC")
    fun getHabitNames(): Flow<List<String>>

    @Query("SELECT * FROM checkins WHERE habitName = :name ORDER BY checkDate DESC")
    fun getByHabit(name: String): Flow<List<CheckIn>>

    @Query("SELECT COUNT(*) FROM checkins WHERE habitName = :name AND checkDate >= :since")
    suspend fun getCountSince(name: String, since: LocalDateTime): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: CheckIn)

    @Update suspend fun update(checkIn: CheckIn)

    @Delete suspend fun delete(checkIn: CheckIn)
}
