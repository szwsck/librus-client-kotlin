package com.wabadaba.dziennik.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.wabadaba.dziennik.api.*
import com.wabadaba.dziennik.api.grades.GradesApi
import com.wabadaba.dziennik.api.grades.GradesRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideLoginClient(rxHttpClient: RxHttpClient) = LoginClient(rxHttpClient)

    @Provides
    @Singleton
    fun provideSharedPrefs(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideUserRepository(context: Context): UserRepository = UserRepository(context)

    @Provides
    @Singleton
    fun provideGradesApi(retrofit: Retrofit): GradesApi = GradesRepository(retrofit)
}