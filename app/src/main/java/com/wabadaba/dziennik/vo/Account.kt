package com.wabadaba.dziennik.vo

import io.requery.Embedded

@Embedded
interface Account {
    val login: String
}