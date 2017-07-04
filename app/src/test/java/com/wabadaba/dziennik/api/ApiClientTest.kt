package com.wabadaba.dziennik.api

import com.nhaarman.mockito_kotlin.MockitoKotlin
import com.wabadaba.dziennik.BaseTest
import com.wabadaba.dziennik.vo.Grade
import io.reactivex.Single
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldEqualTo
import org.joda.time.LocalDateTime
import org.joda.time.Seconds
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ApiClientTest : BaseTest() {
    @Before
    fun init() {
        MockitoKotlin.registerInstanceCreator { RxHttpClient(RuntimeEnvironment.application) }

    }

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

    @Test
    fun shouldFetchEntities() {
        val response = readFile("/endpoints/Grades.json")
        val client = APIClient { Single.just(response) }
        val result = client.fetchEntities<Grade>("")
                .toList()
                .blockingGet()
        result.size shouldEqualTo 5
    }

    @Test
    fun shouldRefreshAccessToken() {
        val response = readFile("/loginResponse.json")
        val client = APIClient { Single.just(response) }
        val result = client.refreshAccess("").blockingGet()
        result.accessToken shouldEqualTo "ACCESS_TOKEN"
        result.refreshToken shouldEqualTo "REFRESH_TOKEN"
        result.expiresIn shouldEqualTo 2592000
        Seconds.secondsBetween(LocalDateTime.now(), result.validFrom).seconds shouldBeLessThan 5
    }
}