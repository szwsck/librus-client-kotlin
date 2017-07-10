package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import io.requery.Entity
import io.requery.Key

@LibrusEntity("Colors")
@Entity
interface Color : Identifiable {

    @get:Key
    override val id: String

    val name: String?

    @get:JsonProperty("RGB")
    val rawColor: String?

}