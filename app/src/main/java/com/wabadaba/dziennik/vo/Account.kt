package com.wabadaba.dziennik.vo

import io.requery.Embedded

@Embedded
interface Account {
    val login: String
    val firstName: String
    val lastName: String
    val groupId: Int
}