package com.wabadaba.dziennik

import org.amshove.kluent.shouldBeInstanceOf
import kotlin.reflect.KClass

abstract class BaseTest {
    protected fun readFile(filename: String) = this.javaClass.getResource(filename)?.readText()
            ?: throw IllegalStateException("File $filename not found")

    fun <T : Any> Any.shouldBeInstanceOf(clazz: KClass<T>, assertions: (T.() -> Unit)) {
        this shouldBeInstanceOf clazz
        @Suppress("UNCHECKED_CAST")
        assertions(this as T)
    }
}