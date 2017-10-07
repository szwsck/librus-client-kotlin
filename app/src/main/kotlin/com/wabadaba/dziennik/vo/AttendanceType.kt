package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Key

@Entity
@LibrusEntity("Attendances/Types")
@JsonDeserialize(`as` = AttendanceTypeEntity::class)
interface AttendanceType : Identifiable {

    @get:Key
    override val id: String

    val name: String?

    @get:JsonProperty("Short")
    val shortName: String?

    @get:JsonProperty("ColorRGB")
    val rawColor: String?

    @get:JsonProperty("IsPresenceKind")
    val presence: Boolean

    @get:JsonProperty("Order") //changed name because 'order' is reserved in sql
    val priority: Int?
}