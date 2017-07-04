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

    val client = APIClient(RxHttpClient(RuntimeEnvironment.application)::executeCall)

    val username = "13335"
    val password = "librus11"

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

    @Test
    fun shouldFetchLogin() {
        val login = LoginService(RuntimeEnvironment.application)
                .login(username, password)
                .blockingGet()
        login shouldEqualTo "13335"
//        val authInfo = client.login(username, password).blockingGet()
//        val me = client.fetchEntity<Me>(authInfo.accessToken).blockingGet()
//        me.account.login shouldEqualTo username
    }
}
