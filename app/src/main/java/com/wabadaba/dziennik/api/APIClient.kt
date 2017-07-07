package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.vo.Identifiable
import com.wabadaba.dziennik.vo.LibrusEntity
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.Request
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


open class APIClient constructor(val httpClient: RxHttpClient) {

    open fun login(username: String, password: String): Single<AuthInfo> {
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

        return httpClient.executeCall(request)
                .map { Parser.parse(it, AuthInfo::class) }
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

        return httpClient.executeCall(request)
                .map { Parser.parse(it, AuthInfo::class) }
    }

    open fun <T : Identifiable> fetchEntities(clazz: KClass<T>, accessToken: String): Observable<T> {
        val librusEntity = clazz.findAnnotation<LibrusEntity>() ?:
                throw IllegalStateException("Class ${clazz.simpleName} not annotated with LibrusEntity annotation")
        return fetchRawData(librusEntity.endpoint, accessToken)
                .flatMapObservable { Parser.parseEntityList(it, clazz.java) }
    }

    fun fetchRawData(endpoint: String, accessToken: String): Single<String> {
        val url = "https://api.librus.pl/2.0/" + endpoint

        val request = Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .get()
                .build()

        return httpClient.executeCall(request)
    }
}


