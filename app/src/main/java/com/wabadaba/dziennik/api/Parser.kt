package com.wabadaba.dziennik.api

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.reactivex.Maybe
import io.reactivex.Observable
import java.io.IOException

object Parser {

    val mapper: ObjectMapper = ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
            .registerModule(JodaModule())
            .registerModule(KotlinModule())

    fun <T> parseEntityList(unescapedInput: String, type: Class<T>): Observable<T> {
        val javaType = mapper.typeFactory.constructParametricType(List::class.java, type)
        return parseEntity<List<T>>(unescapedInput, javaType)
                ?.let { Observable.fromIterable(it) }
                ?: Observable.empty<T>()
    }

    fun <T> parseEntity(unescapedInput: String, type: Class<T>): Maybe<T> {
        val javaType = mapper.typeFactory.constructType(type)
        return parseEntity<T>(unescapedInput, javaType)
                ?.let { Maybe.just(it) }
                ?: Maybe.empty()
    }

    private fun <T> parseEntity(escapedInput: String, type: JavaType): T? {
        val input = escapedInput.unescape()
        try {
            val root = mapper.readTree(input)
            if (!root.containsStandardFields()) {
                throw ParseException(input, "Invalid structure")
            }
            val firstField = root.first()
            if (firstField.isTextual && firstField.textValue() == "Disabled") {
                return null
            }
            return mapper.readValue(mapper.treeAsTokens(firstField), type)

        } catch (e: IOException) {
            throw ParseException(input, e)
        }
    }

    inline fun <reified T> parse(escapedInput: String): T = try {
        mapper.readValue(escapedInput.unescape(), T::class.java)
    } catch (e: IOException) {
        throw ParseException(escapedInput, e)
    }

    private fun JsonNode.containsStandardFields(): Boolean {
        return this.fieldNames()
                .asSequence()
                .toList()
                .containsAll(listOf("Url", "Resources"))
    }

    fun String.unescape() = this.replace("\\\\\\", "\\")
}