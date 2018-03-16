package com.wabadaba.dziennik.api.grades

import com.wabadaba.dziennik.vo.Grade
import io.reactivex.Single
import retrofit2.http.GET


interface GradesRetrofitApi {

    @GET("Grades")
    fun getGrades(): Single<List<Grade>>
}
