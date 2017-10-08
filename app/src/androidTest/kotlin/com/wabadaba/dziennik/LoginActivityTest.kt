package com.wabadaba.dziennik

import android.app.Activity
import android.app.Instrumentation
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.toPackage
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.wabadaba.dziennik.api.*
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

    @get:Rule private val activityRule = IntentsTestRule(LoginActivity::class.java, false, false)
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var apiClientFactory: APIClientFactory
    @Mock private lateinit var loginClient: LoginClient

    private val username = "username"
    private val password = "password"


    @Test
    fun shouldLogIn() {
        val authInfo = AuthInfo("AToken", "RToken")
        `when`(loginClient.login(username, password)).thenReturn(Single.just(authInfo))
        val login = "testlogin"
        val me = MeEntity().apply {
            account.setLogin(login)
            account.setFirstName("Tomasz")
            account.setLastName("Problem")
            account.setGroupId(5)
        } as Me
        val mockApiClient = mock<APIClient> {
            on { fetchEntities(Me::class) } doReturn Observable.just(me)
        }
        `when`(apiClientFactory.create(any(), any())).thenReturn(mockApiClient)
        activityRule.launchActivity(null)
        val packageName = MainApplication::class.java.`package`.name
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)

        Intents.intending(toPackage(packageName)).respondWith(result)
        onView(withId(R.id.usernameEditText)).perform(typeText(username))
        onView(withId(R.id.passwordEditText)).perform(typeText(password))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.logInButton)).perform(click())

        Intents.intended(allOf(toPackage(packageName), hasComponent(MainActivity::class.java.name)))
        verify(userRepository, times(1)).addUser(FullUser(login, "Tomasz", "Problem", 5, authInfo))

    }

}