package com.wabadaba.dziennik.api

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import com.nhaarman.mockito_kotlin.MockitoKotlin
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.wabadaba.dziennik.BaseTest
import io.reactivex.Single
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqualTo
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
        val client = APIClient(RuntimeEnvironment.application) { Single.just(readFile("/loginResponse.json")) }
        val result = client.login("username", "password")
                .blockingGet()

        result.accessToken shouldEqualTo "ACCESS_TOKEN"
        result.refreshToken shouldEqualTo "REFRESH_TOKEN"
    }

    @Test(expected = HttpException.Authorization::class)
    fun shouldPassException() {
        val client = APIClient(RuntimeEnvironment.application) { Single.error(HttpException.Authorization("url")) }
        client.login("username", "password")
                .blockingGet()
    }

    @Test
    fun shouldSaveAndLoadTokens() {
        val client = APIClient(RuntimeEnvironment.application) { Single.error { RuntimeException() } }
        val accessToken = "access"
        val refreshToken = "refresh"
        client.saveTokens(AuthTokens(accessToken, refreshToken))
        val loadTokens = client.loadTokens()!!
        loadTokens.accessToken shouldEqualTo accessToken
        loadTokens.refreshToken shouldEqualTo refreshToken
    }

    @SuppressLint("ApplySharedPref")
    fun shouldNotLoadTokens() {
        val client = APIClient(RuntimeEnvironment.application) { Single.error { RuntimeException() } }
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)
                .edit()
                .clear()    //clear preferences so APIClient cannot read tokens
                .commit()
        client.loadTokens() shouldBe null
    }

    @Test
    fun shouldMakeRequest() {
        val response = readFile("/Grades.json")
        val client = APIClient(RuntimeEnvironment.application) { Single.just(response) }
        val result = client.makeRequest("/Grades.json", AuthTokens("", ""))
                .blockingGet()
        result shouldEqualTo response
    }

    @Test(expected = RuntimeException::class)
    fun shouldFailMakeRequest() {
        val client = APIClient(RuntimeEnvironment.application) { Single.error { RuntimeException() } }
        client.makeRequest("/Grades.json", AuthTokens("", ""))
                .blockingGet()
    }


    @Test
    fun shouldRefreshAccessToken() {
        val response = readFile("/Grades.json")
        val mockHttpClient = mock<RxHttpClient> {
            MockitoKotlin.registerInstanceCreator { RxHttpClient(RuntimeEnvironment.application) }
            on { executeCall(any()) } doReturn listOf(
                    Single.error(HttpException.Unknown(
                            "url",
                            400,
                            readFile("/tokenExpired.json"))),
                    Single.just(readFile("/loginResponse.json")),
                    Single.just(readFile("/Grades.json")))
        }
        val client = APIClient(RuntimeEnvironment.application, mockHttpClient::executeCall)
        val result = client.makeRequest("/Grades.json", AuthTokens("", ""))
                .blockingGet()
        result shouldEqualTo response
    }
}