package com.wabadaba.dziennik

import android.app.Application
import com.bugsnag.android.Bugsnag
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.di.ApplicationModule
import com.wabadaba.dziennik.di.DaggerMainComponent
import com.wabadaba.dziennik.di.MainComponent
import com.wabadaba.dziennik.di.ViewModelModule
import mu.KotlinLogging
import javax.inject.Inject

open class MainApplication : Application() {

    val logger = KotlinLogging.logger { }

    @Inject
    lateinit var client: APIClient

    lateinit var component: MainComponent

    override fun onCreate() {
        logger.error { "CREATE" }
        super.onCreate()
        Bugsnag.init(this)
        component = createMainComponent()
    }

    private fun createMainComponent() = DaggerMainComponent.builder()
            .applicationModule(ApplicationModule(this))
            .viewModelModule(ViewModelModule())
            .build()
}