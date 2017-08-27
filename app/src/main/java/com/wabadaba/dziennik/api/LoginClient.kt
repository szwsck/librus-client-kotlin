package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BuildConfig
import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Request

class LoginClient(val httpClient: RxHttpClient) {

    val HOST = BuildConfig.HOST

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

        val url = HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegments(authEndpoint)
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .url(url)
                .post(formBody)
                .build()

        return httpClient.executeCall(request)
                .map { Parser.parse(it, AuthInfo::class) }
    }

}