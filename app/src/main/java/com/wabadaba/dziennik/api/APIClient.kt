package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.vo.Identifiable
import com.wabadaba.dziennik.vo.LibrusEntity
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.FormBody
import okhttp3.Request
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


open class APIClient(val authInfo: AuthInfo, val httpClient: RxHttpClient) {

    val BASE_URL = BuildConfig.BASE_URL

    open fun <T : Identifiable> fetchEntities(clazz: KClass<T>): Observable<T> {
        val librusEntity = clazz.findAnnotation<LibrusEntity>() ?:
                throw IllegalStateException("Class ${clazz.simpleName} not annotated with LibrusEntity annotation")
        return fetchRawData(librusEntity.endpoint)
                .flatMapObservable { Parser.parseEntityList(it, clazz.java) }
    }

    fun fetchRawData(endpoint: String): Single<String> {
        val url = BASE_URL + "/2.0/" + endpoint

        val request = Request.Builder()
                .addHeader("Authorization", "Bearer " + authInfo.accessToken)
                .url(url)
                .get()
                .build()

        return httpClient.executeCall(request)
    }

    fun refreshAccess(): Single<AuthInfo> {
        val authEndpoint = "/OAuth/Token"
        val auth_token = "MzU6NjM2YWI0MThjY2JlODgyYjE5YTMzZjU3N2U5NGNiNGY="

        val body = FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", authInfo.refreshToken)
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .post(body)
                .url(BASE_URL + authEndpoint)
                .build()

        return httpClient.executeCall(request)
                .map { Parser.parse(it, AuthInfo::class) }
    }
}


