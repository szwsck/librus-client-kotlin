package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wabadaba.dziennik.api.Parser
import io.requery.Embedded
import io.requery.Entity
import io.requery.Key

@Entity
@LibrusEntity("TimetableEntries")
@JsonDeserialize(`as` = TimetableEntryEntity::class)
interface TimetableEntry : Identifiable {

    @get:Key
    override val id: String

    @get:Embedded
    @get:JsonDeserialize(using = ClassroomDeserializer::class)
    val classroom: Classroom?
}

@Embedded
interface Classroom {
    val symbol: String?
    val name: String?
    val size: Int?
}

class ClassroomDeserializer : JsonDeserializer<Classroom>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Classroom? {
        val parsingContext = p.parsingContext
        val timetableEntry = parsingContext.currentValue as TimetableEntry
        val classroom = timetableEntry.classroom
        Parser.mapper.readerForUpdating(classroom)
                .readValue<TimetableEntryEntity_ClassroomEntity>(p, TimetableEntryEntity_ClassroomEntity::class.java)
        return classroom
    }
}