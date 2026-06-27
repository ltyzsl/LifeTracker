package com.lifetracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationHelper {
    const val CHANNEL_COUNTDOWN = "countdown_channel"
    const val CHANNEL_CHECKIN = "checkin_channel"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val countdownChannel = NotificationChannel(
                CHANNEL_COUNTDOWN,
                "倒计时提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "倒计时到期提醒"
                setSound(android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM), null)
            }

            val checkInChannel = NotificationChannel(
                CHANNEL_CHECKIN,
                "打卡提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "习惯打卡提醒"
            }

            notificationManager.createNotificationChannel(countdownChannel)
            notificationManager.createNotificationChannel(checkInChannel)
        }
    }
}
