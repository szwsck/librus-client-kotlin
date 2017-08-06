package com.wabadaba.dziennik

import android.app.Activity
import android.app.Instrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.toPackage
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.nhaarman.mockito_kotlin.times
import com.wabadaba.dziennik.api.APIClient
import com.wabadaba.dziennik.api.AuthInfo
import com.wabadaba.dziennik.api.FullUser
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.ui.MainActivity
import com.wabadaba.dziennik.ui.login.LoginActivity
import com.wabadaba.dziennik.vo.Me
import com.wabadaba.dziennik.vo.MeEntity
import io.reactivex.Observable
import io.reactivex.Single
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class LoginActivityTest : BaseInstrumentedTest() {

    @get:Rule val activityRule = IntentsTestRule(LoginActivity::class.java, false, false)
    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var apiClient: APIClient

    val username = "username"
    val password = "password"


    @Test
    fun shouldLogIn() {
        val authInfo = AuthInfo("AToken", "RToken", 9000000)
        `when`(apiClient.login(username, password)).thenReturn(Single.just(authInfo))
        val login = "testlogin"
        val me = MeEntity().apply {
            account.setLogin(login)
        }
        `when`(apiClient.fetchEntities(Me::class, authInfo.accessToken)).thenReturn(Observable.just(me))
        activityRule.launchActivity(null)
        val packageName = MainApplication::class.java.`package`.name
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)

        // Set up result stubbing when an intent sent to "contacts" is seen.
        Intents.intending(toPackage(packageName)).respondWith(result)
        onView(withId(R.id.usernameEditText)).perform(typeText(username))
        onView(withId(R.id.passwordEditText)).perform(typeText(password))
        onView(withId(R.id.logInButton)).perform(click())

        Intents.intended(allOf(toPackage(packageName), hasComponent(MainActivity::class.java.name)))
        verify(userRepository, times(1)).addUser(FullUser(login, "Tomasz", "Problem", 5, authInfo))

    }

}