package com.wabadaba.dziennik.api

import android.content.Context
import com.wabadaba.dziennik.vo.Me
import io.reactivex.Single

class LoginService(val context: Context) {
    val client = APIClient(RxHttpClient(context)::executeCall)
    fun login(username: String, password: String): Single<String> =
            client.login(username, password)
                    .flatMap { authInfo ->
                        client.fetchEntities(Me::class, authInfo.accessToken)
                                .singleOrError()
                                .map { it.account.login }
                                .doOnSuccess {
                                    AuthInfoRepository(it, context).saveAuthInfo(authInfo)
                                }
                    }
}