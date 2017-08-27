package com.wabadaba.dziennik.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.wabadaba.dziennik.vo.Lesson
import io.reactivex.Observable
import org.joda.time.LocalDate
import java.io.IOException
import kotlin.reflect.KClass

object Parser {

    val mapper: ObjectMapper = ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
            .registerModule(JodaModule())
            .registerModule(KotlinModule())

    fun <T> parseEntityList(escapedInput: String, type: Class<T>): Observable<T> {
        if (type.isAssignableFrom(Lesson::class.java)) {
            val timetable = parseEntity<Timetable>(escapedInput, mapper.typeFactory.constructType(Timetable::class.java))
            println(timetable)
            @Suppress("UNCHECKED_CAST")
            return unpackTimetable(timetable) as Observable<T>
        } else {
            val javaType = mapper.typeFactory.constructParametricType(List::class.java, type)
            return parseEntity<List<T>>(escapedInput, javaType)
                    ?.let { Observable.fromIterable(it) }
                    ?: Observable.empty<T>()
        }
    }

    private fun unpackTimetable(timetable: Timetable?): Observable<Lesson> {
        if (timetable == null) return Observable.empty()
        val result = mutableListOf<Lesson>()
        for ((date, schoolDay) in timetable.entries) {
            for (lessonWrapper in schoolDay) {
                if (lessonWrapper.isNotEmpty()) {
                    val lesson = lessonWrapper[0]
                    lesson.date = date
                    lesson.id = "$date:${lesson.lessonNumber}"
                    result.add(lesson)
                }
            }
        }
        return Observable.fromIterable(result)
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

class Timetable : HashMap<LocalDate, List<List<Lesson>>>()