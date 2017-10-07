package com.wabadaba.dziennik.ui.events

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.ui.monthEnd
import com.wabadaba.dziennik.ui.monthNameNominative
import com.wabadaba.dziennik.ui.multiPut
import com.wabadaba.dziennik.ui.weekEnd
import com.wabadaba.dziennik.vo.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.LocalDate
import org.joda.time.Months
import java.util.*

class EventsViewModel(entityRepo: EntityRepository) : ViewModel() {

    val eventData = MutableLiveData<EventData>()

    init {
        entityRepo.events.observeOn(AndroidSchedulers.mainThread())
                .subscribe { events ->
                    val result = EventData()
                    events.filter { it.date != null }
                            .filter { !it.date!!.isBefore(LocalDate.now()) }
                            .forEach { event ->
                                val date = event.date!!
                                val header = when (event.date) {
                                    LocalDate.now() -> EventHeader(0, "Dzisiaj")
                                    LocalDate.now().plusDays(1) -> EventHeader(1, "Jutro")
                                    in LocalDate.now()..weekEnd() -> EventHeader(2, "Ten tydzień")
                                    in LocalDate.now()..monthEnd() -> EventHeader(3, "Ten miesiąc")
                                    else -> EventHeader(4 + Months.monthsBetween(LocalDate.now(), date).months,
                                            date.monthNameNominative().capitalize())
                                }

                                result.multiPut(header, event)
                            }
                    eventData.value = result
                }
    }
}

class EventData : TreeMap<EventHeader, List<Event>>({ (order1), (order2) -> order1.compareTo(order2) })

data class EventHeader(val order: Int, val title: String)