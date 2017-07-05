package com.wabadaba.dziennik.di

import com.wabadaba.dziennik.api.LiveApiClientTest
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class, ViewModelModule::class))
interface TestMainComponent : MainComponent {
    fun inject(test: LiveApiClientTest)

}