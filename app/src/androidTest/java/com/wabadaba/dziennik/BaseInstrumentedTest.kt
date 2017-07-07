package com.wabadaba.dziennik

import android.arch.lifecycle.ViewModel
import com.nhaarman.mockito_kotlin.KStubbing
import com.nhaarman.mockito_kotlin.mock
import com.wabadaba.dziennik.di.ViewModelFactory
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.`when`

abstract class BaseInstrumentedTest {
    @Suppress("unused")
    @get:Rule val rule = EspressoDaggerMockRule()

    @Mock lateinit var viewModelFactory: ViewModelFactory

    inline fun <reified T : ViewModel> mockViewModel(stubbing: KStubbing<T>.(T) -> Unit): T {
        val viewModelMock = mock<T>(stubbing = stubbing)

        `when`(viewModelFactory.create(T::class.java)).thenReturn(viewModelMock)
        return viewModelMock
    }
}