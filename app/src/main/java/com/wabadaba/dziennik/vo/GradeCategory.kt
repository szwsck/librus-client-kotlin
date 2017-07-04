package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne

@LibrusEntity("Grades/Categories")
@Entity
@JsonDeserialize(`as` = GradeCategoryEntity::class)
interface GradeCategory : Identifiable {

    @get:Key
    override val id: String

    val name: String?

    val weight: Int?

    @get:ManyToOne
    val color: Color?

}