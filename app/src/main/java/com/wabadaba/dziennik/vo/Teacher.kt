package com.wabadaba.dziennik.vo

import io.requery.Entity
import io.requery.Key

@LibrusEntity("Users")
@Entity
interface Teacher : Identifiable {

    @get:Key
    override val id: String

    val firstName: String?

    val lastName: String?

}