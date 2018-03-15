package com.wabadaba.dziennik.di

import android.content.Context
import com.wabadaba.dziennik.MainApplication
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {
    // Binds acts like provide, but it just binds the class instance with its interface
    @Binds
    abstract fun provideContext(application: MainApplication) : Context
}