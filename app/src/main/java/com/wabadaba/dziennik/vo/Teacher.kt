package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key

@Entity
@JsonDeserialize(`as` = TeacherEntity::class)
interface Teacher {
    @get:Key
    val id: String
    val firstName: String?
    val lastName: String?
}