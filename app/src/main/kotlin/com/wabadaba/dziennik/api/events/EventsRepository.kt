package com.wabadaba.dziennik.api.events

import com.wabadaba.dziennik.vo.Event
import com.wabadaba.dziennik.vo.EventCategory
import io.reactivex.Single
import retrofit2.Retrofit

class EventsRepository(val retrofit: Retrofit) : EventsApi {
    private val api by lazy { retrofit.create(EventsRetrofitApi::class.java) }

    override fun getEvents(): Single<List<Event>> = api.getEvents()
    override fun getEventsCategories(): Single<List<EventCategory>> = api.getEventsCategories()
}
