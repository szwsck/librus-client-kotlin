package com.wabadaba.dziennik.vo

import io.requery.Embedded
import io.requery.Entity
import io.requery.Transient

@LibrusEntity("Me")
@Entity
interface Me : Identifiable {

    override val id: String
        @Transient
        get() = account.login

    @get:Embedded
    val account: Account
}