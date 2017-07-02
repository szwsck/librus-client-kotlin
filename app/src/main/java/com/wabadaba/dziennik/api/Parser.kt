package com.wabadaba.dziennik.api

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.IOException

object Parser {

    val mapper: ObjectMapper = ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
            .registerModule(JodaModule())
            .registerModule(KotlinModule())

    fun <T> parseEntityList(unescapedInput: String, type: Class<T>): List<T>? {
        val javaType = mapper.typeFactory.constructParametricType(List::class.java, type)
        return parseEntity(unescapedInput, javaType)
    }

    fun <T> parseEntity(unescapedInput: String, type: Class<T>): T? {
        val javaType = mapper.typeFactory.constructType(type)
        return parseEntity(unescapedInput, javaType)
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