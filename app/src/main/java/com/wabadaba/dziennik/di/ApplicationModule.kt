package com.wabadaba.dziennik.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.api.RxHttpClient
import com.wabadaba.dziennik.api.UserRepository
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
    fun provideApplication(): Application = mainApplication

    @Provides
    fun provideApiClient(rxHttpClient: RxHttpClient) = APIClient(rxHttpClient)

    @Provides
    fun provideSharedPrefs(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun provideUserRepository(context: Context): UserRepository = UserRepository(context)
}