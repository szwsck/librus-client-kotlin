package com.wabadaba.dziennik.api

import org.amshove.kluent.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LiveApiClientTest {

    val login = "13335"
    val password = "librus11"

    @Test
    fun shouldLogIn() {
        val client = APIClient(RxHttpClient(RuntimeEnvironment.application)::executeCall)
        val result = client.login(login, password).blockingGet()
        result.accessToken shouldNotBe null
        result.refreshToken shouldNotBe null
    }

    @Test(expected = HttpException.Authorization::class)
    fun shouldNotLogIn() {
        val client = APIClient(RxHttpClient(RuntimeEnvironment.application)::executeCall)
        client.login(login, "invalid password").blockingGet()
    }
}
