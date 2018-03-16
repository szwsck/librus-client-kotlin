package com.wabadaba.dziennik.api.grades

import com.wabadaba.dziennik.vo.Grade
import io.reactivex.Single

interface GradesApi {
    fun getGrades(): Single<List<Grade>>
}