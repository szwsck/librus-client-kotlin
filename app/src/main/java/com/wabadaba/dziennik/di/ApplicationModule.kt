package com.wabadaba.dziennik.di

import android.app.Application
import android.content.Context
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.api.RxHttpClient
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ApplicationModule(private val mainApplication: Application) {

    @Provides
    @Named("timeout")
    fun provideTimeout(): Long = 30

    @Provides
    fun provideContext(): Context = mainApplication

    @Provides
    fun provideMainApplication(): Application = mainApplication

    @Provides
    fun provideApiClient(rxHttpClient: RxHttpClient) = APIClient(rxHttpClient)
}