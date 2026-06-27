package com.lifetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String = "",
    val isDone: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val dueAt: LocalDateTime? = null,
)
