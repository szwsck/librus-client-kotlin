package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.*


@Entity
@JsonDeserialize(`as` = GradeCategoryEntity::class)
interface GradeCategory : Persistable {

    @get:Key
    var id: String

    var name: String

    var weight: Int?

    @get:ManyToOne
    var color: Color
}