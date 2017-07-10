package com.wabadaba.dziennik.vo

import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne

@LibrusEntity("Grades/Comments")
@Entity
interface GradeComment : Identifiable {

    @get:Key
    override val id: String

    val text: String?

    @get:ManyToOne
    val grade: Grade?

    @get:ManyToOne
    val addedBy: Teacher?

}