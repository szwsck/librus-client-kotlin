package com.wabadaba.dziennik.api

//Full user info. Only available if currently logged in.
class FullUser(
        login: String,
        firstName: String,
        lastName: String,
        groupId: Int,
        val authInfo: AuthInfo)
    : User(login, firstName, lastName, groupId)