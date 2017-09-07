package com.wabadaba.dziennik.ui.events

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.vo.Event

class EventsViewModel(entityRepo: EntityRepository) : ViewModel() {

    val eventsData = MutableLiveData<List<Event>>()

    init {
        entityRepo.events.subscribe {
            eventsData.value = it
        }
    }
}