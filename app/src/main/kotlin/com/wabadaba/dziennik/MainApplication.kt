package com.wabadaba.dziennik

import android.support.multidex.MultiDexApplication
import com.bugsnag.android.Bugsnag
import com.wabadaba.dziennik.di.ApplicationModule
import com.wabadaba.dziennik.di.DaggerMainComponent
import com.wabadaba.dziennik.di.MainComponent

open class MainApplication : MultiDexApplication() {

    lateinit var mainComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        Bugsnag.init(this)
        mainComponent = createMainComponent()
        mainComponent.inject(this)
    }

    private fun createMainComponent() = DaggerMainComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
}