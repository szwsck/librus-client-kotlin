package com.wabadaba.dziennik.api.events

import com.wabadaba.dziennik.vo.Event
import com.wabadaba.dziennik.vo.EventCategory
import io.reactivex.Single

interface EventsApi {
    fun getEvents(): Single<List<Event>>
    fun getEventsCategories(): Single<List<EventCategory>>
}