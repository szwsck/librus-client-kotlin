package com.wabadaba.dziennik.di

import com.wabadaba.dziennik.ui.mainactivity.MainActivity
import com.wabadaba.dziennik.ui.mainactivity.MainActivityFragmentProvider
import com.wabadaba.dziennik.ui.mainactivity.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

// Every activity that extends BaseActivity should be bind there
@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainActivityModule::class, MainActivityFragmentProvider::class])
    abstract fun bindMainActivity() : MainActivity
}
