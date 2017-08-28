package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne
import org.joda.time.LocalDate
import org.joda.time.LocalTime

@Entity
@LibrusEntity("Timetables")
@JsonDeserialize(`as` = LessonEntity::class)
interface Lesson : Identifiable {

    @get:Key
    override var id: String

    @get:JsonProperty("LessonNo")
    val lessonNumber: Int

    @get:ManyToOne
    val subject: Subject?

    @get:ManyToOne
    val teacher: Teacher?

    @get:JsonProperty("IsSubstitutionClass")
    val substitution: Boolean

    @get:JsonProperty("IsCanceled")
    val canceled: Boolean

    @get:ManyToOne
    val orgTeacher: Teacher?

    @get:ManyToOne
    val orgSubject: Subject?

    var date: LocalDate

    val hourFrom: LocalTime

    val hourTo: LocalTime
}