package com.wabadaba.dziennik.api

import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.Request

class APIClient(val httpClient: (Request) -> Single<String>) {

    fun login(username: String, password: String): Single<AuthTokens> {
        val AUTH_URL = "https://api.librus.pl/OAuth/Token"
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
                .url(AUTH_URL)
                .post(formBody)
                .build()

        return httpClient(request).map { Parser.parse<AuthTokens>(it) }
    }

}


