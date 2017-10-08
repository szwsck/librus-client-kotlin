package com.wabadaba.dziennik.api

import io.reactivex.Completable
import io.reactivex.Single
import io.requery.Persistable
import kotlin.reflect.KClass

class RefreshableAPIClient(private val userRepository: UserRepository, private val rxHttpClient: RxHttpClient) {

    lateinit var delegate: APIClient
    lateinit var login: String

    init {
        userRepository.currentUser.subscribe { user ->
            delegate = APIClient(user.authInfo, rxHttpClient)
            login = user.login
        }
    }

    fun refreshIfNeeded(): Completable {
        println("Checking token valid")
        return delegate.fetchRawData("Me").toMaybe().ignoreElement()
                .onErrorResumeNext {
                    //if token is expired, refresh it
                    if (it is HttpException.TokenExpired) {
                        println("Token expired, refreshing...")
                        delegate.refreshAccess()
                                .doOnSuccess { token ->
                                    userRepository.saveAuthInfo(login, token)
                                    delegate = APIClient(token, rxHttpClient)
                                    println("Token refresh successful")
                                }
                                .toCompletable()

                    } else {
                        println("Token refresh failed")
                        Completable.error(it)
                    }
                }
    }


    open fun <T : Persistable> fetchEntities(
            clazz: KClass<T>,
            queryParams: List<Pair<String, String>> = emptyList())
            = delegate.fetchEntities(clazz, queryParams)

    fun pushDevices(token: String): Single<String> = delegate.pushDevices(token)
}