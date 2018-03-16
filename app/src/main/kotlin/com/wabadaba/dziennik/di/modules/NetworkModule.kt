package com.wabadaba.dziennik.di.modules

import android.content.Context
import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.api.*
import com.wabadaba.dziennik.api.notification.LibrusGCMRegistrationManager
import com.wabadaba.dziennik.api.notification.NotificationSender
import com.wabadaba.dziennik.base.ApplicationSchedulers
import com.wabadaba.dziennik.base.Schedulers
import com.wabadaba.dziennik.db.DatabaseManager
import com.wabadaba.dziennik.ui.FragmentRepository
import com.wabadaba.dziennik.ui.GPServicesChecker
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

// Provides network-related classes
@Module
class NetworkModule {

    @Provides
    fun provideApplicationSchedulers(): Schedulers = ApplicationSchedulers()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                // .addInterceptor(ApiInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
            rxJavaCallAdapterFactory: RxJava2CallAdapterFactory,
            jacksonConverterFactory: JacksonConverterFactory): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https;//${BuildConfig.HOST}/2.0/")
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .addConverterFactory(jacksonConverterFactory)
                .build()
    }

    @Provides
    @Singleton
    fun provideJacksonConverterFactory(): JacksonConverterFactory = JacksonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRxJavaCallAdapter(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()


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