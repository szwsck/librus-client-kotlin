package com.wabadaba.dziennik.api

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.wabadaba.dziennik.BaseTest
import com.wabadaba.dziennik.vo.Grade
import io.reactivex.Single
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldEqualTo
import org.joda.time.LocalDateTime
import org.joda.time.Seconds
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ApiClientTest : BaseTest() {

    @Test
    fun shouldLogIn() {
        val httpClient = mock<RxHttpClient> {
            on { executeCall(any()) } doReturn (Single.just(readFile("/loginResponse.json")))
        }
        val client = APIClient(httpClient)
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
        val client = APIClient(httpClient)
        client.login("username", "password")
                .blockingGet()
    }

    @Test
    fun shouldFetchEntities() {
        val response = readFile("/endpoints/Grades.json")
        val httpClient = mock<RxHttpClient> {
            on { executeCall(any()) } doReturn (Single.just(response))
        }
        val client = APIClient(httpClient)
        val result = client.fetchEntities(Grade::class, "")
                .toList()
                .blockingGet()
        result.size shouldEqualTo 5
    }

    @Test
    fun shouldRefreshAccessToken() {
        val response = readFile("/loginResponse.json")
        val httpClient = mock<RxHttpClient> {
            on { executeCall(any()) } doReturn (Single.just(response))
        }
        val client = APIClient(httpClient)
        val result = client.refreshAccess("").blockingGet()
        result.accessToken shouldEqualTo "ACCESS_TOKEN"
        result.refreshToken shouldEqualTo "REFRESH_TOKEN"
        result.expiresIn shouldEqualTo 2592000
        Seconds.secondsBetween(LocalDateTime.now(), result.validFrom).seconds shouldBeLessThan 5
    }
}