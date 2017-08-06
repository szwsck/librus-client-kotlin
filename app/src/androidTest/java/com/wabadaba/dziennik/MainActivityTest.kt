package com.wabadaba.dziennik

import android.app.Activity
import android.app.Instrumentation
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.rule.ActivityTestRule
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.ui.MainActivity
import com.wabadaba.dziennik.ui.login.LoginActivity
import io.reactivex.Observable
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`

class MainActivityTest : BaseInstrumentedTest() {

    @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java, false, false)
    @Mock lateinit var userRepository: UserRepository

    @Test
    fun shouldRedirectToLoginActivity() {
        Intents.init()
        `when`(userRepository.allUsers).thenReturn(Observable.just(emptyList()))
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        Intents.intending(hasComponent(LoginActivity::class.java.name)).respondWith(result)

        activityRule.launchActivity(null)

        Intents.intended(hasComponent(LoginActivity::class.java.name))
        Intents.release()

    }

}