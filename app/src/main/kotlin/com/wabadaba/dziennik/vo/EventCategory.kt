package com.wabadaba.dziennik.vo

import io.requery.Entity
import io.requery.Key
import io.requery.ManyToOne

@Entity
//@LibrusEntity("HomeWorks/Categories")
//@JsonDeserialize(`as` = EventCategoryEntity::class)
interface EventCategory : Identifiable {

    @get:Key
    override val id: String

    val name: String?

    @get:ManyToOne
    val color: Color

}