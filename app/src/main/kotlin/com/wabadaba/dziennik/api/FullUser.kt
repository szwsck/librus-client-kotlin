package com.wabadaba.dziennik.api

import com.fasterxml.jackson.annotation.JsonCreator

//Full user info. Only available if currently logged in.
data class FullUser @JsonCreator constructor(
        val login: String,
        val firstName: String,
        val lastName: String,
        val studentFirstName: String,
        val studentLastName: String,
        val groupId: Int,
        val authInfo: AuthInfo)