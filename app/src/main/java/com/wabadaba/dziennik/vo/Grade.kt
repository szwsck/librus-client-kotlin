package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.*
import org.joda.time.LocalDate

@Entity
@JsonDeserialize(`as` = GradeEntity::class)
interface Grade : Persistable {
    @get:Key
    val id: String

    @get:ManyToOne(cascade = arrayOf(CascadeAction.NONE))
    val category: GradeCategory

    @get:OneToMany(mappedBy = "grade")
    val comments: Set<GradeComment>

    val grade: String

    @get:ManyToOne(cascade = arrayOf(CascadeAction.NONE))
    val addedBy: Teacher?

    val date: LocalDate

    @get:JsonProperty("IsConstituent")
    val isConstituent: Boolean
}