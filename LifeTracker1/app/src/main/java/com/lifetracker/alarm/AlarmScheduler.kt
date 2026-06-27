package com.lifetracker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.lifetracker.receiver.CountdownReceiver

object AlarmScheduler {

    fun scheduleCountdown(
        context: Context,
        countdownId: Long,
        title: String,
        targetTime: java.time.LocalDateTime
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, CountdownReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("countdownId", countdownId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            countdownId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = targetTime.atZone(
            java.time.ZoneId.systemDefault()
        ).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun cancelCountdown(context: Context, countdownId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, CountdownReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            countdownId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
