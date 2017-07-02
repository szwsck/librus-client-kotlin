package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BaseTest
import io.reactivex.Single
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

class ApiClientTest : BaseTest() {
    @Test
    fun shouldLogIn() {
        val client = APIClient { Single.just(readFile("/loginResponse.json")) }
        val result = client.login("username", "password")
                .blockingGet()

        result.accessToken shouldEqualTo "ACCESS_TOKEN"
        result.refreshToken shouldEqualTo "REFRESH_TOKEN"
    }

    @Test(expected = HttpException.Authorization::class)
    fun shouldPassException() {
        val client = APIClient { Single.error(HttpException.Authorization("url")) }
        client.login("username", "password")
                .blockingGet()
    }
}