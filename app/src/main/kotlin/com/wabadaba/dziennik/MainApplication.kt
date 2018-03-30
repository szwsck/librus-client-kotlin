package com.wabadaba.dziennik

import android.annotation.SuppressLint
import android.content.Context
import android.support.multidex.MultiDex
import com.bugsnag.android.Bugsnag
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.api.notification.LibrusGCMRegistrationManager
import com.wabadaba.dziennik.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

open class MainApplication : DaggerApplication()  {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    @Inject
    lateinit var gcmRegistrationManager: LibrusGCMRegistrationManager

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    @Inject
    lateinit var userRepository: UserRepository

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()
        Bugsnag.init(this)
    }
}