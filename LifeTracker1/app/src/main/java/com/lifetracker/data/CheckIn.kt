package com.lifetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "checkins")
data class CheckIn(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitName: String,
    val checkDate: LocalDateTime,
    val note: String = "",
)
