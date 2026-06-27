package com.lifetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "countdowns")
data class Countdown(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetTime: LocalDateTime,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
