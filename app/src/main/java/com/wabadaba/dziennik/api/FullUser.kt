package com.wabadaba.dziennik.api

//Full user info. Only available if currently logged in.
data class FullUser(
        val login: String,
        val firstName: String,
        val lastName: String,
        val groupId: Int,
        val authInfo: AuthInfo)