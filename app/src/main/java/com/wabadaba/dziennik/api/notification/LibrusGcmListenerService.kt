package com.wabadaba.dziennik.api.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import com.google.android.gms.gcm.GcmListenerService
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.ui.MainActivity


@Suppress("DEPRECATION")
class LibrusGcmListenerService : GcmListenerService() {
    override fun onMessageReceived(p0: String, data: Bundle) {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefs.getBoolean("enable_notifications", true)) {
            println("received message but notifications are disabled")
            return
        }

        println("received message, sending notification...")

        val message = data.getString("message")
        val user = data.getString("userId")

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_school_black_24dp)
                .setContentTitle(message)
                .setContentText(user)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}