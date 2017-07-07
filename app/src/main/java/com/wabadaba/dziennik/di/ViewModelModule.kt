package com.wabadaba.dziennik.di

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.ui.login.LoginViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider


@Module
class ViewModelModule {

    @Provides
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun provideLoginViewModel(application: Application, client: APIClient): ViewModel =
            LoginViewModel(application, client)

    @Provides
    fun provideViewModelFactory(creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) =
            ViewModelFactory(creators)
}