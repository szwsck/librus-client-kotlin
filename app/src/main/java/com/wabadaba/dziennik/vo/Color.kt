package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.*

@Entity
@JsonDeserialize(`as` = ColorEntity::class)
interface Color: Persistable {
    @get:Key
    var id:String

    var name:String

    @get:JsonProperty("RGB")
    var rawColor:String

}