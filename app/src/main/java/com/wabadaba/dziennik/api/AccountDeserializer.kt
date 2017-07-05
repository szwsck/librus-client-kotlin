package com.wabadaba.dziennik.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.wabadaba.dziennik.vo.Account
import com.wabadaba.dziennik.vo.Me
import com.wabadaba.dziennik.vo.MeEntity_AccountEntity

class AccountDeserializer : JsonDeserializer<Account>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Account {
        val parsingContext = p.parsingContext
        val me = parsingContext.currentValue as Me
        val account = me.account
        Parser.mapper.readerForUpdating(account).readValue<MeEntity_AccountEntity>(p, MeEntity_AccountEntity::class.java)
        return account
    }
}