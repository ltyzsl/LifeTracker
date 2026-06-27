package com.lifetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Database(entities = [Todo::class, Countdown::class, CheckIn::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun countdownDao(): CountdownDao
    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lifetracker.db"
                ).build().also { INSTANCE = it }
            }
    }
}

class Converters {
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter fun fromLocalDateTime(value: LocalDateTime?): String? = value?.format(fmt)
    @TypeConverter fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it, fmt) }
}
