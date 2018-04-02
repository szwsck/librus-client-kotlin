package com.wabadaba.dziennik.api.luckynumbers

import com.wabadaba.dziennik.vo.LuckyNumber
import io.reactivex.Single
import retrofit2.Retrofit

class LuckyNumbersRepository(val retrofit: Retrofit) : LuckyNumbersApi {
    private val api by lazy { retrofit.create(LuckyNumbersRetrofitApi::class.java) }

    override fun getLuckyNumbers(): Single<List<LuckyNumber>> = api.getLuckyNumbers()
}
