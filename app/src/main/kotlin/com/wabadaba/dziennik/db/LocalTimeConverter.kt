package com.wabadaba.dziennik.db

import io.requery.Converter
import org.joda.time.LocalTime

class LocalTimeConverter : Converter<LocalTime, Int> {

    override fun getMappedType(): Class<LocalTime> {
        return LocalTime::class.java
    }

    override fun getPersistedType(): Class<Int> {
        return Int::class.java
    }

    override fun getPersistedSize(): Int? {
        return null
    }

    override fun convertToPersisted(value: LocalTime?): Int? {
        if (value == null) {
            return null
        }
        return value.millisOfDay

    }

    override fun convertToMapped(type: Class<out LocalTime>, value: Int?): LocalTime? {
        if (value == null) {
            return null
        }
        return LocalTime.fromMillisOfDay(value.toLong())
    }
}