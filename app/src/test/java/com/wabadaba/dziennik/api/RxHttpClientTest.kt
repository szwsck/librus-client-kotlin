package com.wabadaba.dziennik.api

import android.content.Context
import android.net.ConnectivityManager
import android.security.NetworkSecurityPolicy
import com.wabadaba.dziennik.BaseTest
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import java.util.concurrent.TimeUnit


@RunWith(RobolectricTestRunner::class)
@Config(shadows = arrayOf(RxHttpClientTest.MyNetworkSecurityPolicy::class))
class RxHttpClientTest : BaseTest() {

    val server = MockWebServer().apply { start() }
    val request: Request = Request.Builder()
            .url(server.url("somePath"))
            .get()
            .build()

    @Test(expected = HttpException.DeviceOffline::class)
    fun shouldHandleOfflineMode() {
        val connectivityManager = RuntimeEnvironment.application
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val shadow = Shadows.shadowOf(connectivityManager.activeNetworkInfo)
        shadow.setConnectionStatus(false)

        RxHttpClient(RuntimeEnvironment.application, 1)
                .executeCall(request)
                .blockingGet()
    }

    @Test
    fun shouldMakeCall() {
        val response = "RESPONSE"
        server.enqueue(MockResponse().setBody(response))

        val result = RxHttpClient(RuntimeEnvironment.application, 1)
                .executeCall(request)
                .blockingGet()

        result shouldEqualTo response
    }

    @Test(expected = HttpException.DeviceOffline::class)
    fun shouldHandleTimeout() {
        val response = "RESPONSE"
        server.enqueue(MockResponse()
                .setBody(response)
                .throttleBody(1, 10, TimeUnit.SECONDS))

        RxHttpClient(RuntimeEnvironment.application, timeoutSeconds = 1)
                .executeCall(request)
                .blockingGet()
    }

    @Test(expected = HttpException.Maintenance::class)
    fun shouldHandleMaintenance() {
        val response = readFile("/Maintenance.json")
        server.enqueue(MockResponse().setBody(response).setResponseCode(503))

        RxHttpClient(RuntimeEnvironment.application, 1)
                .executeCall(request)
                .blockingGet()
    }

    @Test(expected = HttpException.NotActive::class)
    fun shouldHandleNotActive() {
        val response = readFile("/NotActive.json")
        server.enqueue(MockResponse().setBody(response).setResponseCode(400))

        RxHttpClient(RuntimeEnvironment.application, 1)
                .executeCall(request)
                .blockingGet()
    }

    @Test(expected = HttpException.ServerOffline::class)
    fun shouldHandleServerOffline() {
        val response = "Server offline"
        server.enqueue(MockResponse().setBody(response).setResponseCode(500))

        RxHttpClient(RuntimeEnvironment.application, 1)
                .executeCall(request)
                .blockingGet()
    }

    @Test(expected = HttpException.Unknown::class)
    fun shouldHandleUnknownResponse() {
        val response = "Unknown response!!!1!!1 "
        server.enqueue(MockResponse().setBody(response).setResponseCode(500))

        RxHttpClient(RuntimeEnvironment.application, 1)
                .executeCall(request)
                .blockingGet()
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Implements(NetworkSecurityPolicy::class)
    class MyNetworkSecurityPolicy {

        @Implementation
        fun isCleartextTrafficPermitted(hostname: String) = true

        companion object {
            @JvmStatic
            @Implementation
            fun getInstance() = MyNetworkSecurityPolicy::class.java
                    .classLoader.loadClass("android.security.NetworkSecurityPolicy")
                    .newInstance() as NetworkSecurityPolicy
        }
    }
}