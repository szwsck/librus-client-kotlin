package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key

@LibrusEntity("Users")
@Entity
@JsonDeserialize(`as` = TeacherEntity::class)
interface Teacher : Identifiable {

    @get:Key
    override val id: String

    val firstName: String?

    val lastName: String?

}