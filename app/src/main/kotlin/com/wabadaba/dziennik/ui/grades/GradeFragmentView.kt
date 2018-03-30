package com.wabadaba.dziennik.ui.grades

import com.wabadaba.dziennik.base.BaseView
import com.wabadaba.dziennik.vo.Grade

interface GradeFragmentView : BaseView {
    fun showGrades(grades : List<Grade>)
}