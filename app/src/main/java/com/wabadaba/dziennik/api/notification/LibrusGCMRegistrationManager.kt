package com.wabadaba.dziennik.api.notification

import android.content.Context
import android.preference.PreferenceManager
import com.bugsnag.android.Bugsnag
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.api.FullUser
import com.wabadaba.dziennik.api.RxHttpClient
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class LibrusGCMRegistrationManager(private val currentUser: FullUser,
                                   rxHttpClient: RxHttpClient,
                                   val context: Context) {

    private val senderId = BuildConfig.SENDER_ID

    private val pref_key = "gcm_registered_users"
    private val apiClient = APIClient(currentUser.authInfo, rxHttpClient)

    fun register() {
        if (!isRegistered()) {
            getRegistrationToken()
                    .flatMap { regToken -> apiClient.pushDevices(regToken) }
                    .doOnSuccess { this.setRegistered() }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {
                                println("GCM registration successful")
                            },
                            { e ->
                                println("GCM registration failed")
                                Bugsnag.notify(e)
                            })
        } else {
            println("User $currentUser already registered for GCM")
        }
    }


    private fun isRegistered(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getStringSet(pref_key, emptySet())
                .contains(currentUser.login)
    }

    private fun setRegistered() {
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

