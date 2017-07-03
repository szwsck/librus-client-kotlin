package com.wabadaba.dziennik.api

import android.content.Context
import android.preference.PreferenceManager
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import mu.KotlinLogging
import okhttp3.FormBody
import okhttp3.Request


class APIClient(val context: Context, val httpClient: (Request) -> Single<String>) {

    private val ACCESS_TOKEN_PREF = "access_token"
    private val REFRESH_TOKEN_PREF = "refresh_token"

    private val logger = KotlinLogging.logger {}

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

        return httpClient(request)
                .map { Parser.parse<AuthTokens>(it) }
                .doOnSuccess(this::saveTokens)
    }

    private fun refreshAccess(refreshToken: String): Single<AuthTokens> {
        val AUTH_URL = "https://api.librus.pl/OAuth/Token"
        val auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY="

        val body = FormBody.Builder()
                .add("grant_type", REFRESH_TOKEN_PREF)
                .add(REFRESH_TOKEN_PREF, refreshToken)
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .post(body)
                .url(AUTH_URL)
                .build()

        return httpClient(request)
                .map { Parser.parse<AuthTokens>(it) }
                .doOnSuccess(this::saveTokens)
    }

    private fun fetchData(endpoint: String, accessToken: String): Single<String> {
        val url = "https://api.librus.pl/2.0" + endpoint

        val request = Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .get()
                .build()
        return httpClient(request)
    }

    fun makeRequest(endpoint: String, tokens: AuthTokens): Single<String> {
        return fetchData(endpoint, tokens.accessToken)
                .onErrorResumeNext { cause ->
                    if (isTokenExpired(cause)) {
                        refreshAccess(tokens.refreshToken)
                                .flatMap { (accessToken) -> fetchData(endpoint, accessToken) }
                    } else {
                        Single.error(cause)
                    }
                }.subscribeOn(Schedulers.io())
    }


    fun saveTokens(tokens: AuthTokens) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(ACCESS_TOKEN_PREF, tokens.accessToken)
                .putString(REFRESH_TOKEN_PREF, tokens.refreshToken)
                .apply()
    }

    fun loadTokens(): AuthTokens? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (prefs.contains(ACCESS_TOKEN_PREF) && prefs.contains(REFRESH_TOKEN_PREF)) {
            return AuthTokens(
                    prefs.getString(ACCESS_TOKEN_PREF, null),
                    prefs.getString(REFRESH_TOKEN_PREF, null))
        } else {
            return null
        }
    }

    private fun isTokenExpired(throwable: Throwable): Boolean = throwable.message?.contains("Access Token expired") ?: false
}


