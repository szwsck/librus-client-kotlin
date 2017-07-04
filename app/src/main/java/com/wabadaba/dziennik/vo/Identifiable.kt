package com.wabadaba.dziennik.vo

import io.requery.Persistable

interface Identifiable : Persistable {
    val id: String
}