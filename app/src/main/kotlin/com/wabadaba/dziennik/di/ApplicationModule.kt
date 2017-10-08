package com.wabadaba.dziennik.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.wabadaba.dziennik.api.*
import com.wabadaba.dziennik.api.notification.LibrusGCMRegistrationManager
import com.wabadaba.dziennik.db.DatabaseManager
import com.wabadaba.dziennik.ui.FragmentRepository
import com.wabadaba.dziennik.ui.GPServicesChecker
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

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
    fun provideEntityRepository(context: Context, userRepository: UserRepository, apiClient: RefreshableAPIClient): EntityRepository
            = EntityRepository(userRepository.currentUser,
            { user -> DatabaseManager(context, user).dataStore },
            apiClient)

    @Provides
    @Singleton
    fun provideRefreshableAPIClient(userRepository: UserRepository, httpClient: RxHttpClient) =
            RefreshableAPIClient(userRepository, httpClient)

    @Provides
    @Singleton
    fun provideFragmentRepository(context: Context): FragmentRepository = FragmentRepository(context)

    @Provides
    @Singleton
    fun provideAPIClientFactory(): APIClientFactory = APIClientFactory()

    @Provides
    @Singleton
    fun provideGCMRegistrationManager(apiClient: RefreshableAPIClient, context: Context): LibrusGCMRegistrationManager
            = LibrusGCMRegistrationManager(context, apiClient)

    @Provides
    @Singleton
    fun provideGPServicesChecker(): GPServicesChecker = GPServicesChecker()
}