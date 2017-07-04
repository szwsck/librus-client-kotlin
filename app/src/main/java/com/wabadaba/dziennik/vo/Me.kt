package com.wabadaba.dziennik.vo

import io.requery.Embedded
import io.requery.Entity
import io.requery.Persistable

//@JsonDeserialize(`as` = MeEntity::class)
@LibrusEntity("Me")
@Entity
interface Me : Persistable {

    //    @get:JsonDeserialize(using = AccountDeserializer::class)
    @get:Embedded
    val account: Account
}

//class AccountDeserializer : JsonDeserializer<Account>() {
//    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Account {
//        val parentMe = p!!.parsingContext.currentValue as Me
//        val account = parentMe.account
//        return ObjectMapper().readValue()
//                .readValue(p, Account::class.java)
//    }
//}