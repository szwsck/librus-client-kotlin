package com.wabadaba.dziennik.api

import com.wabadaba.dziennik.BaseTest
import com.wabadaba.dziennik.di.ApplicationModule
import com.wabadaba.dziennik.di.DaggerTestMainComponent
import org.amshove.kluent.shouldNotBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(23))
class LiveLoginClientTest : BaseTest() {

    @Inject lateinit var client: LoginClient

    @Before
    fun setup() {
        DaggerTestMainComponent.builder()
                .applicationModule(ApplicationModule(RuntimeEnvironment.application))
                .build()
                .inject(this)
    }

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

}
