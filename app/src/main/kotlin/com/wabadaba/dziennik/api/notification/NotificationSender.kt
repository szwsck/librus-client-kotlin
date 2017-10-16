package com.wabadaba.dziennik.api.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi


class NotificationSender(private val context: Context) {
    private val notificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    companion object {
        val NOTIFICATION_CHANNEL_ID = "com.wabadaba.dziennik.notifications"
        val NOTIFICATION_TAG = "com.wabadaba.dziennik"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.setShowBadge(true)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun updateNotification(id : Int, notification: Notification) {
        notificationManager.notify(NOTIFICATION_TAG, id, notification)
    }

}