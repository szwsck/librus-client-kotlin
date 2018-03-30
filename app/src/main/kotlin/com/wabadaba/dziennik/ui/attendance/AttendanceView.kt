package com.wabadaba.dziennik.ui.attendance

import com.wabadaba.dziennik.base.BaseView
import com.wabadaba.dziennik.vo.Attendance

interface AttendanceView : BaseView {
    fun showAttendance(attendance : List<Attendance>)
}