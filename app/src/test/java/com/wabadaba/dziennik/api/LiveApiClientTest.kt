package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BaseTest
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LiveApiClientTest : BaseTest() {

    val login = "13335"
    val password = "librus11"

    @Test
    fun shouldLogIn() {
        val client = APIClient(RuntimeEnvironment.application, RxHttpClient(RuntimeEnvironment.application)::executeCall)
        val result = client.login(login, password).blockingGet()
        result.accessToken shouldNotBe null
        result.refreshToken shouldNotBe null
    }

    @Test(expected = HttpException.Authorization::class)
    fun shouldNotLogIn() {
        val client = APIClient(RuntimeEnvironment.application, RxHttpClient(RuntimeEnvironment.application)::executeCall)
        client.login(login, "invalid password").blockingGet()
    }

    @Test
    fun shouldMakeRequest() {
        val client = APIClient(RuntimeEnvironment.application, RxHttpClient(RuntimeEnvironment.application)::executeCall)
        val tokens = client.login(login, password)
                .blockingGet()
        val result = client.makeRequest("/Grades", tokens)
                .blockingGet()
        val expected = readFile("/Root.json")
        result shouldEqualTo expected
    }
}
