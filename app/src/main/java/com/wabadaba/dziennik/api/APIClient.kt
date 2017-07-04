package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.vo.LibrusEntity
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.Request
import kotlin.reflect.full.findAnnotation


class APIClient(val httpClient: (Request) -> Single<String>) {

    fun login(username: String, password: String): Single<AuthInfo> {
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
                .map { Parser.parse<AuthInfo>(it) }
    }

    fun refreshAccess(refreshToken: String): Single<AuthInfo> {
        val AUTH_URL = "https://api.librus.pl/OAuth/Token"
        val auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY="

        val body = FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .post(body)
                .url(AUTH_URL)
                .build()

        return httpClient(request)
                .map { Parser.parse<AuthInfo>(it) }
    }

    inline fun <reified T> fetchEntity(accessToken: String): Maybe<T> {
        val librusEntity = T::class.findAnnotation<LibrusEntity>() ?:
                throw IllegalStateException("Class ${T::class.simpleName} not annotated with LibrusEntity annotation")
        return fetchRawData(librusEntity.endpoint, accessToken)
                .flatMapMaybe { Parser.parseEntity(it, T::class.java) }
    }

    inline fun <reified T> fetchEntities(accessToken: String): Observable<T> {
        val librusEntity = T::class.findAnnotation<LibrusEntity>() ?:
                throw IllegalStateException("Class ${T::class.simpleName} not annotated with LibrusEntity annotation")
        return fetchRawData(librusEntity.endpoint, accessToken)
                .flatMapObservable { Parser.parseEntityList(it, T::class.java) }
    }

    fun fetchRawData(endpoint: String, accessToken: String): Single<String> {
        val url = "https://api.librus.pl/2.0/" + endpoint

        val request = Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .get()
                .build()

        return httpClient(request)
    }
}


