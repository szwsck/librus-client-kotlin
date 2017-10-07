package com.wabadaba.dziennik.db

import io.requery.Converter
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate

class LocalDateConverter : Converter<LocalDate, Long> {

    override fun getMappedType(): Class<LocalDate> {
        return LocalDate::class.java
    }

    override fun getPersistedType(): Class<Long> {
        return Long::class.java
    }

    override fun getPersistedSize(): Int? {
        return null
    }

    override fun convertToPersisted(value: LocalDate?): Long? {
        if (value == null) {
            return null
        }
        return value.toDateTimeAtStartOfDay(DateTimeZone.UTC).millis
    }

    override fun convertToMapped(type: Class<out LocalDate>, value: Long?): LocalDate? {
        if (value == null) {
            return null
        }
        return LocalDate(value, DateTimeZone.UTC)
    }
}