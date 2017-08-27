package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.requery.Entity
import io.requery.ForeignKey
import io.requery.OneToOne
import io.requery.Transient

@Entity
@LibrusEntity("Grades/Averages")
@JsonDeserialize(`as` = AverageEntity::class)
interface Average : Identifiable {

    override val id: String
        @Transient
        get() = subject.id

    @get:OneToOne
    @get:ForeignKey
    val subject: Subject

    val semester1: Float

    val semester2: Float

    val fullYear: Float
}