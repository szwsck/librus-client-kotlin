package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key
import io.requery.Persistable

@LibrusEntity("Colors")
@Entity
@JsonDeserialize(`as` = ColorEntity::class)
interface Color : Persistable {

    @get:Key
    val id: String

    val name: String?

    @get:JsonProperty("RGB")
    val rawColor: String?

}