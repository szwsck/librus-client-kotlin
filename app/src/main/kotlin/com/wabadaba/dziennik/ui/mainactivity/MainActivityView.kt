package com.wabadaba.dziennik.ui.mainactivity

import com.wabadaba.dziennik.base.BaseView
import com.wabadaba.dziennik.vo.LuckyNumber
import com.wabadaba.dziennik.api.User

interface MainActivityView : BaseView {
    fun initUsers(users : List<User>)

    fun addLuckyNumberSection(luckyNumber : LuckyNumber?)
}