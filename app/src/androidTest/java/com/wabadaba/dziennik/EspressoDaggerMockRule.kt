package com.wabadaba.dziennik

import android.support.test.InstrumentationRegistry
import com.wabadaba.dziennik.di.ApplicationModule
import com.wabadaba.dziennik.di.MainComponent
import it.cosenonjaviste.daggermock.DaggerMockRule

class EspressoDaggerMockRule : DaggerMockRule<MainComponent>(
        MainComponent::class.java,
        ApplicationModule(getApp())) {
    init {
        set { component -> getApp().mainComponent = component }
    }

    companion object {
        fun getApp(): MainApplication = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MainApplication
    }
}