package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.wabadaba.dziennik.api.AccountDeserializer
import io.requery.Embedded
import io.requery.Entity
import io.requery.Transient

@Entity
@LibrusEntity("Me")
@JsonDeserialize(`as` = MeEntity::class)
interface Me : Identifiable {

    override val id: String
        @Transient
        get() = account.login

    @get:Embedded
    @get:JsonDeserialize(using = AccountDeserializer::class)
    val account: Account

}

