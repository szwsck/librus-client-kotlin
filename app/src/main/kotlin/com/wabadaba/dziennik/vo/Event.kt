package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

@Entity
//@LibrusEntity("HomeWorks")
//@JsonDeserialize(`as` = EventEntity::class)
interface Event : Identifiable {

    @get:Key
    override val id: String

    val content: String?

    val date: LocalDate?

    @get:ManyToOne
    val category: EventCategory?

    @get:JsonProperty("LessonNo")
    val lessonNumber: Int?

    @get:JsonProperty("CreatedBy")
    @get:ManyToOne
    val addedBy: Teacher?

    @get:ManyToOne
    val subject: Subject?

    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val addDate: LocalDateTime?
}

