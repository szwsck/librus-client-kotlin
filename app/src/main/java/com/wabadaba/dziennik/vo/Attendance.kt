package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime


@Entity
@LibrusEntity("Attendances")
@QueryParam("showPresences", "false")
@JsonDeserialize(`as` = AttendanceEntity::class)
interface Attendance : Identifiable {

    @get:Key
    override val id: String

    @get:JsonProperty("LessonNo")
    val lessonNumber: Int?

    val semester: Int?

    @get:ManyToOne
    val lesson: PlainLesson?

    @get:ManyToOne
    val type: AttendanceType?

    val date: LocalDate?

    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val addDate: LocalDateTime?

    @get:ManyToOne
    val addedBy: Teacher?
}

