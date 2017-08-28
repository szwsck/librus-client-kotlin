package com.wabadaba.dziennik.api

import android.content.Context
import android.net.ConnectivityManager
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

//Open for mockito
open class RxHttpClient @Inject constructor(
        val context: Context,
        @Named("timeout")
        val timeoutSeconds: Long) {

    private val logger = KotlinLogging.logger {}

    open fun executeCall(request: Request): Single<String> = Single.create<String> {
        val url = request.url().toString()
        try {
            if (isDeviceOffline()) {
                it.onError(HttpException.DeviceOffline(url))
            } else {
                logger.info { "start fetching data from $url" }
                val response = OkHttpClient.Builder()
                        .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .build()
                        .newCall(request).execute()
                val message = response.body()?.string() ?: throw IllegalStateException("Empty response from $url")
                if (response.isSuccessful) {
                    logger.info { "data fetched successfully from $url" }
                    it.onSuccess(message)
                } else {
                    it.onError(createException(response.code(), message, url))
                }
            }
        } catch (e: SocketTimeoutException) {
            it.onError(HttpException.DeviceOffline(url))
        }
    }.subscribeOn(Schedulers.io())

    private fun isDeviceOffline(): Boolean {
        val activeNetworkInfo = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = activeNetworkInfo.activeNetworkInfo
        return networkInfo == null || !networkInfo.isConnectedOrConnecting
    }

    private fun createException(code: Int, message: String, url: String): HttpException {
        try {
            val root = Parser.mapper.readTree(message) ?: return HttpException.Unknown(url, code, message)
            if (root.hasChildWithText("error", "invalid_grant")) {
                return HttpException.Authorization(url)
            }
            if (root.hasChildWithText("Code", "NotActive")) {
                return HttpException.NotActive(url)
            }
            if (root.hasChildWithText("Status", "Maintenance")) {
                return HttpException.Maintenance(url)
            }
        } catch (e: JsonParseException) {
            //message is not a valid json, ignore
        }
        if (message == "Server offline") {
            return HttpException.ServerOffline(url)
        }
        return HttpException.Unknown(url, code, message)
    }

    private fun JsonNode.hasChildWithText(childName: String, text: String): Boolean {
        val child = this.at("/$childName")
        return !child.isMissingNode && child.textValue().contains(text)
    }
}