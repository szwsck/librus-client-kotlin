package com.wabadaba.dziennik.ui.events

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.BasePresenter
import com.wabadaba.dziennik.base.Schedulers
import com.wabadaba.dziennik.ui.monthEnd
import com.wabadaba.dziennik.ui.monthNameNominative
import com.wabadaba.dziennik.ui.multiPut
import com.wabadaba.dziennik.ui.weekEnd
import com.wabadaba.dziennik.vo.Event
import org.joda.time.LocalDate
import org.joda.time.Months
import java.util.*

class EventData : TreeMap<EventHeader, List<Event>>({ (order1), (order2) -> order1.compareTo(order2) })

data class EventHeader(val order: Int, val title: String)

class EventsFragmentPresenter(val schedulers: Schedulers, val entityRepository: EntityRepository) : BasePresenter<EventsFragmentView>() {
    fun getEvents() {
        compositeObservable.add(
                entityRepository.events.subscribeOn(schedulers.backgroundThread())
                        .observeOn(schedulers.mainThread())
                        .subscribe({
                            val result = EventData()
                            it.filter { it.date != null }
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
                            view?.showEventData(result)
                        }, { view?.showErrorDialog(it) })
        )
    }
}