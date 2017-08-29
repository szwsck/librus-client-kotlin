package com.wabadaba.dziennik.api.notification

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.api.RxHttpClient
import com.wabadaba.dziennik.api.UserRepository
import io.reactivex.functions.Consumer
import javax.inject.Inject


class LibrusRegistrationIntentService : IntentService("LibrusRegistrationService") {

    @Inject lateinit var userRepo: UserRepository
    @Inject lateinit var httpClient: RxHttpClient

    private val senderId = BuildConfig.SENDER_ID

    override fun onHandleIntent(p0: Intent?) {

        val mainApplication = application as MainApplication
        mainApplication.mainComponent.inject(this)

        val authInfo = userRepo.currentUser.blockingFirst().authInfo
        val apiClient = APIClient(authInfo, httpClient)

        val instanceID = InstanceID.getInstance(this)
        val token = instanceID.getToken(senderId,
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)

        println("Received registration token from GCM: $token")

        apiClient.pushDevices(token)
                .subscribe(Consumer { println("Token sent to server, $it") })
    }
}