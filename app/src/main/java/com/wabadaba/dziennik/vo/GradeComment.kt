package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.*

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