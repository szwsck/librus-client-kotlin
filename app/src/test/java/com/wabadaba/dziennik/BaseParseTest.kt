package com.wabadaba.dziennik

import com.wabadaba.dziennik.api.Parser
import kotlin.reflect.KClass

abstract class BaseParseTest : BaseTest() {
    fun <T : Any> parse(filename: String, clazz: KClass<T>) = Parser.parseEntity(readFile(filename), clazz.java)
    fun <T : Any> parseList(filename: String, clazz: KClass<T>) = Parser.parseEntityList(readFile(filename), clazz.java)
}