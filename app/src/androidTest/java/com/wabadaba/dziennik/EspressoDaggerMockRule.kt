package com.wabadaba.dziennik

import android.support.test.InstrumentationRegistry
import com.wabadaba.dziennik.di.ApplicationModule
import com.wabadaba.dziennik.di.MainComponent
import com.wabadaba.dziennik.di.ViewModelModule
import it.cosenonjaviste.daggermock.DaggerMockRule

class EspressoDaggerMockRule : DaggerMockRule<MainComponent>(
        MainComponent::class.java,
        ApplicationModule(getApp()),
        ViewModelModule()) {
    init {
        set { component -> getApp().component = component }
    }

    companion object {
        fun getApp(): MainApplication = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MainApplication
    }
}