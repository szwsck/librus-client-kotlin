package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne

@LibrusEntity("Grades/Comments")
@Entity
@JsonDeserialize(`as` = GradeCommentEntity::class)
interface GradeComment : Identifiable {

    @get:Key
    override val id: String

    val text: String?

    @get:ManyToOne
    val grade: Grade?

    @get:ManyToOne
    val addedBy: Teacher?

}