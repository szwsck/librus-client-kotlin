package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.annotation.JsonProperty
import io.requery.Key
import org.joda.time.LocalDate

@LibrusEntity("LuckyNumbers")
//@Entity
//@JsonDeserialize(`as` = LuckyNumberEntity::class)     //FIXME handle disabled
interface LuckyNumber : Identifiable {

    @get:JsonProperty("LuckyNumber")
    val number: Int?

    @get:JsonProperty("LuckyNumberDay")
    val date: LocalDate?

    override val id: String
        @get:Key
        get() = this.date.toString()
}