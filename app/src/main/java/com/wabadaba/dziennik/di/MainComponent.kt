package com.wabadaba.dziennik.di

import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.ui.login.LoginActivity
import dagger.Component

@Component(modules = arrayOf(ViewModelModule::class, ApplicationModule::class))
interface MainComponent {

    fun inject(mainApplication: MainApplication)

    fun inject(loginActivity: LoginActivity)

}