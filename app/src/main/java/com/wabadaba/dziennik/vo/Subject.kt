package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key

@Entity
@LibrusEntity("Subjects")
@JsonDeserialize(`as` = SubjectEntity::class)
interface Subject : Identifiable {

    @get:Key
    override val id: String

    val name: String?

}