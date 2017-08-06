package com.wabadaba.dziennik.di

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.ui.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider


@Module
class ViewModelModule {

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel(appliaction: Application): ViewModel = MainViewModel(appliaction)

    @Provides
    fun provideViewModelFactory(creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) =
            ViewModelFactory(creators)
}