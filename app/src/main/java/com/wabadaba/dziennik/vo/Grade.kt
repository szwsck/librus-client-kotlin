package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne
import io.requery.OneToMany
import org.joda.time.LocalDate

@LibrusEntity("Grades")
@Entity
interface Grade : Identifiable {

    @get:Key
    override val id: String

    @get:ManyToOne
    val category: GradeCategory?

    @get:OneToMany
    val comments: Set<GradeComment>

    val grade: String?

    @get:ManyToOne
    val addedBy: Teacher?

    val date: LocalDate?

    @get:JsonProperty("IsConstituent")
    val isConstituent: Boolean?

}