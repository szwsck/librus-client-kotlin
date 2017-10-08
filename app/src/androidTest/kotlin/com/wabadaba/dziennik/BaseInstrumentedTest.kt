package com.wabadaba.dziennik

import android.arch.lifecycle.ViewModel
import android.support.test.InstrumentationRegistry
import com.nhaarman.mockito_kotlin.KStubbing
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.GPServicesChecker
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.`when`

abstract class BaseInstrumentedTest {
    @Suppress("unused")
    @get:Rule
    val rule = EspressoDaggerMockRule()

    @Mock lateinit var viewModelFactory: ViewModelFactory

    @Mock lateinit var servicesChecker: GPServicesChecker
    @Before
    fun setup() {
        `when`(servicesChecker.check(any()))
                .doReturn(true)
    }

    inline fun <reified T : ViewModel> mockViewModel(stubbing: KStubbing<T>.(T) -> Unit): T {
        val viewModelMock = mock<T>(stubbing = stubbing)

        `when`(viewModelFactory.create(T::class.java)).thenReturn(viewModelMock)
        return viewModelMock
    }

    companion object {
        fun getApp(): MainApplication = InstrumentationRegistry.getInstrumentation()
                .targetContext
                .applicationContext as MainApplication
    }
}