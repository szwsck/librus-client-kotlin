package com.wabadaba.dziennik.api.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.google.android.gms.gcm.GcmListenerService
import com.wabadaba.dziennik.R

@Suppress("DEPRECATION")
class LibrusGcmListenerService : GcmListenerService() {
    override fun onMessageReceived(p0: String, data: Bundle) {
        println("Received GCM message")
        val message = data.getString("message")
        val user = data.getString("userId")
        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_school_black_24dp)
                .setContentTitle(message)
                .setContentText(user)
                .build()
        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager.notify(1, notification)
    }
}