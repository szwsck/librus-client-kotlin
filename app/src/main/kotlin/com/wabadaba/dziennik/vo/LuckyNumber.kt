package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.Persistable
import org.joda.time.LocalDate

@LibrusEntity("LuckyNumbers")
@Entity
@JsonDeserialize(`as` = LuckyNumberEntity::class)
interface LuckyNumber : Persistable {

    @get:JsonProperty("LuckyNumber")
    val number: Int?

    @get:JsonProperty("LuckyNumberDay")
    val date: LocalDate?
}