package com.wabadaba.dziennik.api.luckynumbers

import com.wabadaba.dziennik.vo.LuckyNumber
import io.reactivex.Single

interface LuckyNumbersApi {
    fun getLuckyNumbers(): Single<List<LuckyNumber>>
}