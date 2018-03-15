package com.wabadaba.dziennik.di.modules

import android.content.Context
import com.wabadaba.dziennik.api.*
import com.wabadaba.dziennik.api.notification.LibrusGCMRegistrationManager
import com.wabadaba.dziennik.api.notification.NotificationSender
import com.wabadaba.dziennik.db.DatabaseManager
import com.wabadaba.dziennik.ui.FragmentRepository
import com.wabadaba.dziennik.ui.GPServicesChecker
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

// Provides network-related classes
@Module
class NetworkModule {
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

    @Provides
    fun provideNotificationHelper(context: Context) = NotificationSender(context)

    @Provides
    @Named("timeout")
    fun provideTimeout(): Long = 30
}