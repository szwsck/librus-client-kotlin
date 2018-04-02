package com.wabadaba.dziennik.api.grades

import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.GradeCategory
import io.reactivex.Single
import retrofit2.Retrofit

class GradesRepository(val retrofit: Retrofit) : GradesApi {
    private val api by lazy { retrofit.create(GradesRetrofitApi::class.java) }

    override fun getGrades(): Single<List<Grade>> = api.getGrades()
    override fun getGradesCategory(): Single<GradeCategory> = api.getGradesCategory()
}
