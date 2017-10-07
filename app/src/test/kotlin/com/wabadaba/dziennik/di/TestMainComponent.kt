package com.wabadaba.dziennik.di

import com.wabadaba.dziennik.api.LiveLoginClientTest
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(ApplicationModule::class, ViewModelModule::class))
@Singleton
interface TestMainComponent : MainComponent {
    fun inject(test: LiveLoginClientTest)

}