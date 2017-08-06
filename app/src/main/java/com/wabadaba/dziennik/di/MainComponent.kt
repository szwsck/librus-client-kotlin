package com.wabadaba.dziennik.di

import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.ui.MainActivity
import com.wabadaba.dziennik.ui.login.LoginActivity
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class, ViewModelModule::class))
interface MainComponent {
    fun inject(mainApplication: MainApplication)
    fun inject(loginActivity: LoginActivity)
    fun inject(mainActivity: MainActivity)
}