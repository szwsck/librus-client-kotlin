package com.wabadaba.dziennik.di

import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.ui.MainActivity
import com.wabadaba.dziennik.ui.grades.GradesFragment
import com.wabadaba.dziennik.ui.login.LoginActivity
import dagger.Component
import javax.inject.Singleton

@Component(modules = arrayOf(ApplicationModule::class, ViewModelModule::class))
@Singleton
interface MainComponent {
    fun inject(mainApplication: MainApplication)
    fun inject(loginActivity: LoginActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(gradesFragment: GradesFragment)
}