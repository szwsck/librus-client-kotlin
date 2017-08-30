package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne

@Entity
@LibrusEntity("Lessons")
@JsonDeserialize(`as` = PlainLessonEntity::class)
interface PlainLesson : Identifiable {

    @get:Key
    override val id: String

    @get:ManyToOne
    val teacher: Teacher?

    @get:ManyToOne
    val subject: Subject?
}