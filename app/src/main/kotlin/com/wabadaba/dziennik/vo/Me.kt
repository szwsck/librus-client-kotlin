package com.wabadaba.dziennik.vo

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import io.requery.Embedded
import io.requery.Entity
import io.requery.Persistable


@Entity
@LibrusEntity("Me")
@JsonDeserialize(`as` = MeEntity::class, using = MeDeserializer::class)
interface Me : Persistable {

    @get:Embedded
    val account: Account

    @get:Embedded
    val user: User

}

class MeDeserializer : StdDeserializer<Me>(Me::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Me {
        val rootNode = p.codec.readTree<JsonNode>(p)
        val accountNode = rootNode.get("Account")
        val userNode = rootNode.get("User")
        return MeEntity().apply {
            with(account) {
                setLogin(accountNode.get("Login").asText())
                setFirstName(accountNode.get("FirstName").asText())
                setLastName(accountNode.get("LastName").asText())
                setGroupId(accountNode.get("GroupId").asInt())
            }
            with(user) {
                setFirstName(userNode.get("FirstName").asText())
                setLastName(userNode.get("LastName").asText())
            }
        }
    }
}

@Embedded
interface Account {
    val login: String
    val firstName: String
    val lastName: String
    val groupId: Int
}

@Embedded
interface User {
    val firstName: String
    val lastName: String
}