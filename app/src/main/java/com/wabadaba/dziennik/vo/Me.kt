package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wabadaba.dziennik.api.AccountDeserializer
import io.requery.Embedded
import io.requery.Entity
import io.requery.Key

@Entity
@LibrusEntity("Me")
interface Me : Identifiable {

    override val id: String
        @Key
        get() = account.login

    @get:Embedded
    @get:JsonDeserialize(using = AccountDeserializer::class)
    val account: Account

}

