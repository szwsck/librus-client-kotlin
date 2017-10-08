package com.wabadaba.dziennik

import android.support.multidex.MultiDexApplication
import com.bugsnag.android.Bugsnag
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.api.notification.LibrusGCMRegistrationManager
import com.wabadaba.dziennik.di.ApplicationModule
import com.wabadaba.dziennik.di.DaggerMainComponent
import com.wabadaba.dziennik.di.MainComponent
import javax.inject.Inject

open class MainApplication : MultiDexApplication() {

    lateinit var mainComponent: MainComponent

    @Inject
    lateinit var gcmRegistrationManager: LibrusGCMRegistrationManager

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate() {
        super.onCreate()
        Bugsnag.init(this)
        mainComponent = createMainComponent()
        mainComponent.inject(this)
        userRepository.currentUser
                .subscribe(gcmRegistrationManager::register)
    }

    private fun createMainComponent() = DaggerMainComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
}