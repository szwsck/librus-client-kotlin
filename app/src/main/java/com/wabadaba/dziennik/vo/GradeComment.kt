package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne
import io.requery.Persistable

@LibrusEntity("Grades/Comments")
@Entity
@JsonDeserialize(`as` = GradeCommentEntity::class)
interface GradeComment : Persistable {

    @get:Key
    val id: String

    val text: String?

    @get:ManyToOne
    val grade: Grade?

    @get:ManyToOne
    val addedBy: Teacher?

}