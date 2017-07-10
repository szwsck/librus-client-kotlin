package com.wabadaba.dziennik.api

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.wabadaba.dziennik.vo.Models
import io.reactivex.Observable
import java.io.IOException
import kotlin.reflect.KClass

object Parser {

    private val requeryModule: Module = SimpleModule()
            .apply {
                Models.DEFAULT.types.forEach {
                    @Suppress("UNCHECKED_CAST")
                    addAbstractTypeMapping<Any>(it.baseType as Class<Any>?, it.classType)
                    Unit
                }
            }

    val mapper: ObjectMapper = ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
            .registerModule(requeryModule)
            .registerModule(JodaModule())
            .registerModule(KotlinModule())

    fun <T> parseEntityList(unescapedInput: String, type: Class<T>): Observable<T> {
        val javaType = mapper.typeFactory.constructParametricType(List::class.java, type)
        return parseEntity<List<T>>(unescapedInput, javaType)
                ?.let { Observable.fromIterable(it) }
                ?: Observable.empty<T>()
    }

    private fun <T> parseEntity(escapedInput: String, type: JavaType): T? {
        val input = escapedInput.unescape()
        try {
            val root = mapper.readTree(input)
            val firstField = root.first()
            if (firstField.isTextual && firstField.textValue() == "Disabled") {
                return null
            }
            return mapper.readValue(mapper.treeAsTokens(firstField), type)

        } catch (e: IOException) {
            throw ParseException(input, e)
        }
    }

    fun <T : Any> parse(escapedInput: String, clazz: KClass<T>): T = try {
        mapper.readValue(escapedInput.unescape(), clazz.java)
    } catch (e: IOException) {
        throw ParseException(escapedInput, e)
    }

    fun String.unescape() = this.replace("\\\\\\", "\\")
}