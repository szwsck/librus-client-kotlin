package com.wabadaba.dziennik.api

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.IOException

object EntityParser {

    private val mapper = ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
            .registerModule(JodaModule())
            .registerModule(KotlinModule())


    fun <T> parseList(unescapedInput: String, type: Class<T>): List<T>? {
        val javaType = mapper.typeFactory.constructParametricType(List::class.java, type)
        return parseObject(unescapedInput, javaType)
    }

    fun <T> parseObject(unescapedInput: String, type: Class<T>): T? {
        val javaType = mapper.typeFactory.constructType(type)
        return parseObject(unescapedInput, javaType)
    }


    private fun <T> parseObject(unescapedInput: String, type: JavaType): T? {
        val input = unescapedInput.replace("\\\\\\", "\\")
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

    private fun JsonNode.containsStandardFields(): Boolean {
        return this.fieldNames()
                .asSequence()
                .toList()
                .containsAll(listOf("Url", "Resources"))
    }
}