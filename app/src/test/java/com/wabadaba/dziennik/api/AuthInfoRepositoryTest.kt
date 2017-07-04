package com.wabadaba.dziennik.api

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AuthInfoRepositoryTest {

    val repo = AuthInfoRepository("login", RuntimeEnvironment.application)

    val authInfo = AuthInfo("aToken", "rToken", 12)

    @Test
    fun shouldSaveAndRead() {
        repo.saveAuthInfo(authInfo)
        val read = repo.getAuthInfo()
        read shouldEqual authInfo
    }

    @Test
    fun shouldUpdate() {
        repo.saveAuthInfo(authInfo)
        val updated = authInfo.copy(accessToken = "newToken")
        repo.saveAuthInfo(updated)
        val read = repo.getAuthInfo()
        read shouldEqual updated
        read shouldNotEqual authInfo
    }

    @Test
    fun shouldDelete() {
        val info = authInfo
        repo.saveAuthInfo(info)
        repo.deleteAuthInfo()
        Assertions.assertThatThrownBy {
            repo.getAuthInfo()
        }.isInstanceOf(IllegalStateException::class.java)
    }
}