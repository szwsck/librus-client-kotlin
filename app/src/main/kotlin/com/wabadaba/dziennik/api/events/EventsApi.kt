package com.wabadaba.dziennik.api.events

import com.wabadaba.dziennik.vo.Event
import io.reactivex.Single

interface EventsApi {
    fun getEvents(): Single<List<Event>>
}