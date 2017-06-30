package com.wabadaba.dziennik

import com.wabadaba.dziennik.api.EntityParser
import kotlin.reflect.KClass

abstract class BaseParseTest {
    fun <T : Any> parse(filename: String, clazz: KClass<T>) = EntityParser.parseObject(readFile(filename), clazz.java)
    fun <T : Any> parseList(filename: String, clazz: KClass<T>) = EntityParser.parseList(readFile(filename), clazz.java)

    private fun readFile(filename: String): String {
        return this.javaClass.getResource(filename).readText()
    }
}