package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BuildConfig
import com.wabadaba.dziennik.vo.LibrusEntity
import io.reactivex.Observable
import io.reactivex.Single
import io.requery.Persistable
import okhttp3.*
import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


open class APIClient(private val authInfo: AuthInfo, private val httpClient: RxHttpClient) {

    private val HOST = BuildConfig.HOST

    private val jsonMediaType = MediaType.parse("application/json; charset=utf-8")

    open fun <T : Persistable> fetchEntities(clazz: KClass<T>, queryParams: List<Pair<String, String>>): Observable<T> {
        val librusEntity = clazz.findAnnotation<LibrusEntity>() ?:
                throw IllegalStateException("Class ${clazz.simpleName} not annotated with LibrusEntity annotation")
        return fetchRawData(librusEntity.endpoint, queryParams)
                .flatMapObservable { Parser.parseEntityList(it, clazz.java) }
    }

    open fun <T : Persistable> fetchEntities(clazz: KClass<T>) = fetchEntities(clazz, emptyList())


    private fun fetchRawData(endpoint: String, queryParams: List<Pair<String, String>>): Single<String> {
        val urlBuilder = HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegment("2.0")
                .addPathSegments(endpoint)

        queryParams.forEach { (name, value) ->
            urlBuilder.addQueryParameter(name, value)
        }

        val url = urlBuilder.build()

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

        val url = HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegment(authEndpoint)
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Basic " + auth_token)
                .post(body)
                .url(url)
                .build()

        return httpClient.executeCall(request)
                .map { Parser.parse(it, AuthInfo::class) }
    }

    fun pushDevices(token: String): Single<String> {
        val endpoint = "/2.0/PushDevices"

        val bodyJson = JSONObject().put("provider", "Android_dru")
                .put("device", token)

        val body = RequestBody.create(jsonMediaType, bodyJson.toString())

        val url = HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegments(endpoint)
                .build()

        val request = Request.Builder()
                .addHeader("Authorization", "Bearer " + authInfo.accessToken)
                .post(body)
                .url(url)
                .build()

        return httpClient.executeCall(request)
    }
}


