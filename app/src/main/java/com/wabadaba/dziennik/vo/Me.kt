package com.wabadaba.dziennik.vo

import io.requery.Embedded
import io.requery.Entity
import io.requery.Persistable

@LibrusEntity("Me")
@Entity
interface Me : Persistable {

    @get:Embedded
    val account: Account
}