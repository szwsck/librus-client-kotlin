package com.wabadaba.dziennik

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import com.nhaarman.mockito_kotlin.doReturn
import com.wabadaba.dziennik.ui.login.LoginActivity
import com.wabadaba.dziennik.ui.login.LoginViewModel
import io.reactivex.Completable
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify

class LoginActivityTest : BaseInstrumentedTest() {

    @get:Rule val activityRule = ActivityTestRule(LoginActivity::class.java, false, false)

    @Test
    fun shouldLogIn() {
        val username = "username"
        val password = "password"

        val mock = mockViewModel<LoginViewModel> {
            on { login(anyString(), anyString()) } doReturn Completable.complete()
        }

        activityRule.launchActivity(null)
        onView(withId(R.id.usernameEditText)).perform(typeText(username))
        onView(withId(R.id.passwordEditText)).perform(typeText(password))
        onView(withId(R.id.logInButton)).perform(click())

        verify(mock).login(username, password)
    }

}