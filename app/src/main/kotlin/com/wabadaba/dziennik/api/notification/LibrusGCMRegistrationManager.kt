package com.wabadaba.dziennik.api.notification

import android.content.Context
import android.preference.PreferenceManager
import com.bugsnag.android.Bugsnag
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.api.FullUser
import com.wabadaba.dziennik.api.RefreshableAPIClient
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class LibrusGCMRegistrationManager(val context: Context, private val apiClient: RefreshableAPIClient) {

    private val senderId = BuildConfig.SENDER_ID

    private val pref_key = "gcm_registered_users"

    fun register(currentUser: FullUser) {
        if (!isRegistered(currentUser)) {
            getRegistrationToken()
                    .flatMap { regToken -> apiClient.pushDevices(regToken) }
                    .doOnSuccess { this.setRegistered(currentUser) }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {
                                println("GCM registration successful")
                            },
                            { e ->
                                println("GCM registration failed")
                                Bugsnag.notify(e)
                            })
        }
    }


    private fun isRegistered(currentUser: FullUser): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getStringSet(pref_key, emptySet())
                .contains(currentUser.login)
    }

    private fun setRegistered(currentUser: FullUser) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val registered = prefs.getStringSet(pref_key, emptySet())
        prefs.edit().putStringSet(pref_key, registered.plus(currentUser.login))
                .apply()
    }

    private fun getRegistrationToken() = Single.fromCallable {
        val instanceID = InstanceID.getInstance(context)
        instanceID.getToken(senderId,
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)
    }
}

