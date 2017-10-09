package com.wabadaba.dziennik.ui.announcements

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.ui.monthNameNominative
import com.wabadaba.dziennik.ui.multiPut
import com.wabadaba.dziennik.vo.Announcement
import io.reactivex.android.schedulers.AndroidSchedulers
import org.joda.time.LocalDate
import org.joda.time.Months
import java.util.*

class AnnouncementsViewModel(entityRepo: EntityRepository) : ViewModel() {

    val announcementData = MutableLiveData<AnnouncementData>()

    init {
        entityRepo.announcements.observeOn(AndroidSchedulers.mainThread())
                .subscribe { announcements ->
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
                    announcementData.value = result
                }
    }
}

class AnnouncementData : TreeMap<AnnouncementHeader, List<Announcement>>({ (order1), (order2) -> order1.compareTo(order2) })

data class AnnouncementHeader(val order: Int, val title: String)