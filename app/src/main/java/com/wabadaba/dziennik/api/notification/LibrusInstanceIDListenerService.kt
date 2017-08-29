package com.wabadaba.dziennik.api.notification

import android.content.Intent
import com.google.android.gms.iid.InstanceIDListenerService


class LibrusInstanceIDListenerService : InstanceIDListenerService() {
    override fun onTokenRefresh() {
        val intent = Intent(this, LibrusRegistrationIntentService::class.java)
        startService(intent)
    }
}