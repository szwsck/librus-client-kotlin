package com.wabadaba.dziennik.api

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.wabadaba.dziennik.BaseTest
import io.reactivex.Single
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class LoginClientTest : BaseTest() {

    @Test
    fun shouldLogIn() {
        val httpClient = mock<RxHttpClient> {
            on { executeCall(any()) } doReturn (Single.just(readFile("/loginResponse.json")))
        }
        val client = LoginClient(httpClient)
        val result = client.login("username", "password")
                .blockingGet()

        result.accessToken shouldEqualTo "ACCESS_TOKEN"
        result.refreshToken shouldEqualTo "REFRESH_TOKEN"
    }

    @Test(expected = HttpException.Authorization::class)
    fun shouldPassException() {
        val httpClient = mock<RxHttpClient> {
            on { executeCall(any()) } doReturn (Single.error(HttpException.Authorization("url")))
        }
        val client = LoginClient(httpClient)
        client.login("username", "password")
                .blockingGet()
    }
}