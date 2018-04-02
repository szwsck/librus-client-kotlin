package com.wabadaba.dziennik.api.events

import com.wabadaba.dziennik.vo.Event
import com.wabadaba.dziennik.vo.EventCategory
import io.reactivex.Single
import retrofit2.http.GET


interface EventsRetrofitApi {

    @GET("HomeWorks")
    fun getEvents(): Single<List<Event>>

    @GET("HomeWorks/Categories")
    fun getEventsCategories(): Single<List<EventCategory>>
}
