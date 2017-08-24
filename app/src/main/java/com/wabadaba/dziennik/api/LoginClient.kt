package com.wabadaba.dziennik.api

import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.Request

class LoginClient(val httpClient: RxHttpClient) {
    //    val BASE_URL = "https://api.librus.pl"
    val BASE_URL = "https://librus-mock.herokuapp.com" //FIXME

    fun login(username: String, password: String): Single<AuthInfo> {
        val authEndpoint = "/OAuth/Token"
        val auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY="
        val formBody = FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("grant_type", "password")
                .add("librus_long_term_token", "1")
                .add("librus_rules_accepted", "true")
                .add("librus_mobile_rules_accepted", "true")
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .url(BASE_URL + authEndpoint)
                .post(formBody)
                .build()

        return httpClient.executeCall(request)
                .map { Parser.parse(it, AuthInfo::class) }
    }

}