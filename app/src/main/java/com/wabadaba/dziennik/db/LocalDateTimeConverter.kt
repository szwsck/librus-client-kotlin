package com.wabadaba.dziennik.db

import io.requery.Converter
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime

class LocalDateTimeConverter : Converter<LocalDateTime, Long> {

    override fun getMappedType(): Class<LocalDateTime> {
        return LocalDateTime::class.java
    }

    override fun getPersistedType(): Class<Long> {
        return Long::class.java
    }

    override fun getPersistedSize(): Int? {
        return null
    }

    override fun convertToPersisted(value: LocalDateTime?): Long? {
        if (value == null) {
            return null
        }
        return value.toDateTime(DateTimeZone.UTC).millis

    }

    override fun convertToMapped(type: Class<out LocalDateTime>, value: Long?): LocalDateTime? {
        if (value == null) {
            return null
        }
        return LocalDateTime(value, DateTimeZone.UTC)
    }
}