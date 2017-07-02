package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.Persistable

@LibrusEntity("Users")
@Entity
@JsonDeserialize(`as` = TeacherEntity::class)
interface Teacher: Persistable {

    @get:Key
    val id: String

    val firstName: String?

    val lastName: String?

}