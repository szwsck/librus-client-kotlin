package com.wabadaba.dziennik.api

import com.fasterxml.jackson.annotation.JsonCreator

//FullUser info without auth tokens, used to display in the drawer
data class User @JsonCreator constructor(
        val login: String,
        val firstName: String,
        val lastName: String,
        val studentFirstName: String,
        val studentLastName: String,
        val groupId: Int
)