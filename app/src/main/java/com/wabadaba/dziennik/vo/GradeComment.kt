package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.*

@Entity
@JsonDeserialize(`as` = GradeCommentEntity::class)
interface GradeComment : Persistable {
    @get:Key
    val id: String

    val text: String

    @get:ManyToOne(cascade = arrayOf(CascadeAction.NONE))
    val grade: Grade

    @get:ManyToOne(cascade = arrayOf(CascadeAction.NONE))
    val addedBy: Teacher
}