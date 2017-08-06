package com.wabadaba.dziennik.api

//FullUser info without auth tokens, used to display in the drawer
open class User(
        val login: String,
        val firstName: String,
        val lastName: String,
        val groupId: Int)