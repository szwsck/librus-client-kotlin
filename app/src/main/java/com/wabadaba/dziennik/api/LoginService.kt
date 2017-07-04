package com.wabadaba.dziennik.api

import android.content.Context
import io.reactivex.Single
import org.json.JSONObject

class LoginService(val context: Context) {
    val client = APIClient(RxHttpClient(context)::executeCall)
    fun login(username: String, password: String): Single<String> =
            client.login(username, password)
                    .flatMap { authInfo ->
                        //TODO remove workaround for jackson being unable to deserialize embedded requery entities
                        client.fetchRawData("Me", authInfo.accessToken)
                                .map { JSONObject(it) }
                                .map {
                                    it.getJSONObject("Me")
                                            .getJSONObject("Account")
                                            .getString("Login")
                                }
                                .doOnSuccess { login ->
                                    AuthInfoRepository(login, context).saveAuthInfo(authInfo)
                                }
                    }
}