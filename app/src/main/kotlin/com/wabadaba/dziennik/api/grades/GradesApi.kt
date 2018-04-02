package com.wabadaba.dziennik.api.grades

import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.GradeCategory
import com.wabadaba.dziennik.vo.GradeComment
import io.reactivex.Single

interface GradesApi {
    fun getGrades(): Single<List<Grade>>
    fun getGradesCategories(): Single<List<GradeCategory>>
    fun getGradesComments(): Single<List<GradeComment>>
}