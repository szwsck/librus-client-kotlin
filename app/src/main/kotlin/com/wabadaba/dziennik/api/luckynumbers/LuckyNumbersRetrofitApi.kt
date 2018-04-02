package com.wabadaba.dziennik.api.luckynumbers

import com.wabadaba.dziennik.vo.LuckyNumber
import io.reactivex.Single
import retrofit2.http.GET


interface LuckyNumbersRetrofitApi {

    @GET("LuckyNumbers")
    fun getLuckyNumbers(): Single<List<LuckyNumber>>

}
