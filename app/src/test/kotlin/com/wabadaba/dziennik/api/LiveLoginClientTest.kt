package com.wabadaba.dziennik.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldNotBe
import org.junit.Ignore
import org.junit.Test

@Ignore
class LiveLoginClientTest {
    private val networkInfoMock = mock<NetworkInfo> {
        on { isConnectedOrConnecting } doReturn true
    }
    private val connectivityManagerMock = mock<ConnectivityManager> {
        on { activeNetworkInfo } doReturn networkInfoMock
    }
    private val contextMock = mock<Context> {
        on { getSystemService(Context.CONNECTIVITY_SERVICE) } doReturn connectivityManagerMock
    }
    private val client: LoginClient = LoginClient(RxHttpClient(contextMock, 60))

    private val username = "13335"
    private val password = "librus11"

    @Test
    fun shouldLogIn() {
        val result = client.login(username, password).blockingGet()
        result.accessToken shouldNotBe null
        result.refreshToken shouldNotBe null
    }

    @Test(expected = HttpException.Authorization::class)
    fun shouldNotLogIn() {
        client.login(username, "invalid password").blockingGet()
    }

}
