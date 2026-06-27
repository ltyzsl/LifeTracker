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
interface TodoDao {
    @Query("SELECT * FROM todos WHERE isDone = 0 ORDER BY createdAt DESC")
    fun getActive(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE isDone = 1 ORDER BY createdAt DESC")
    fun getDone(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update suspend fun update(todo: Todo)

    @Delete suspend fun delete(todo: Todo)
}
