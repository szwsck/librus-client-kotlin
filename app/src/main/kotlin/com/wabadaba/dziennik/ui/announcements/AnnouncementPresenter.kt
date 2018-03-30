package com.wabadaba.dziennik.ui.announcements

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.BasePresenter
import com.wabadaba.dziennik.base.Schedulers
import com.wabadaba.dziennik.ui.monthNameNominative
import com.wabadaba.dziennik.ui.multiPut
import com.wabadaba.dziennik.vo.Announcement
import org.joda.time.LocalDate
import org.joda.time.Months
import java.util.*

class AnnouncementData : TreeMap<AnnouncementHeader, List<Announcement>>({ (order1), (order2) -> order1.compareTo(order2) })

data class AnnouncementHeader(val order: Int, val title: String)

class AnnouncementPresenter(val schedulers: Schedulers, val entityRepository: EntityRepository) : BasePresenter<AnnouncementView>() {
    fun getAnnouncementData() {
        compositeObservable.add(
        entityRepository.announcements
                .observeOn(schedulers.mainThread())
                .subscribeOn(schedulers.backgroundThread())
                .subscribe ({ announcements ->
                    val result = AnnouncementData()
                    announcements.filter { it.addDate != null }
                            .forEach { announcement ->
                                val date = announcement.addDate!!.toLocalDate()
                                val header = when (date) {
                                    LocalDate.now() -> AnnouncementHeader(0, "Dzisiaj")
                                    in LocalDate.now().minusDays(LocalDate.now().dayOfWeek - 1) .. LocalDate.now() ->
                                        AnnouncementHeader(1, "Ten tydzień")
                                    in LocalDate.now().minusDays(LocalDate.now().dayOfMonth - 1) .. LocalDate.now() ->
                                        AnnouncementHeader(2, "Ten miesiąc")
                                    else -> AnnouncementHeader(3 + Months.monthsBetween(date, LocalDate.now()).months,
                                            date.monthNameNominative().capitalize())
                                }

                                result.multiPut(header, announcement)
                            }
                    view?.showAnnouncements(result)
                }, { view?.showErrorDialog(it) })
        )
    }
}