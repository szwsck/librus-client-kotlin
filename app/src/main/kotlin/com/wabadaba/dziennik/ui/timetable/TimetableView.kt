package com.wabadaba.dziennik.ui.timetable

import com.wabadaba.dziennik.base.BaseView

interface TimetableView : BaseView {
    fun showTimetable(timetable : Timetable)
}