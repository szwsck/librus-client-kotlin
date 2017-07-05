package com.wabadaba.dziennik.ui.login

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.api.AuthInfoRepository
import com.wabadaba.dziennik.vo.Me
import io.reactivex.Completable
import mu.KotlinLogging
import javax.inject.Inject

class LoginViewModel @Inject constructor(
        application: Application,
        val client: APIClient) : AndroidViewModel(application) {

    val logger = KotlinLogging.logger { }

    fun login(username: String, password: String): Completable =
            client.login(username, password)
                    .flatMap { authInfo ->
                        client.fetchEntities(Me::class, authInfo.accessToken)
                                .singleOrError()
                                .map { it.account.login }
                                .doOnSuccess {
                                    logger.info { authInfo }
                                    AuthInfoRepository(it, getApplication()).saveAuthInfo(authInfo)
                                }
                    }.toCompletable()
}
