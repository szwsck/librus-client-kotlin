package com.wabadaba.dziennik.api

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.wabadaba.dziennik.BaseTest
import com.wabadaba.dziennik.vo.Grade
import io.reactivex.Single
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ApiClientTest : BaseTest() {

    private val authInfo = AuthInfo("", "")

    @Test
    fun shouldFetchEntities() {
        val response = readFile("/endpoints/Grades.json")
        val httpClient = mock<RxHttpClient> {
            on { executeCall(any()) } doReturn (Single.just(response))
        }
        val client = APIClient(authInfo, httpClient)
        val result = client.fetchEntities(Grade::class)
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
        val client = APIClient(authInfo, httpClient)
        val result = client.refreshAccess().blockingGet()
        result.accessToken shouldEqualTo "ACCESS_TOKEN"
        result.refreshToken shouldEqualTo "REFRESH_TOKEN"
    }
}