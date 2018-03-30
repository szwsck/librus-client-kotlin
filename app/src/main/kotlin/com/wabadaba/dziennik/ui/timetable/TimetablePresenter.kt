package com.wabadaba.dziennik.ui.timetable

import com.github.debop.kodatimes.days
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.BasePresenter
import com.wabadaba.dziennik.base.Schedulers
import com.wabadaba.dziennik.ui.forDateRange
import com.wabadaba.dziennik.vo.Event
import com.wabadaba.dziennik.vo.Lesson
import io.reactivex.rxkotlin.Observables
import org.joda.time.LocalDate

data class TimetableLesson(val lesson: Lesson, val event: Event?)

class Timetable : LinkedHashMap<LocalDate, SchoolDay?>() {
    var empty = true

    override fun put(key: LocalDate, value: SchoolDay?): SchoolDay? {
        empty = false
        return super.put(key, value)
    }
}

class SchoolDay : LinkedHashMap<Int, TimetableLesson?>()

class TimetablePresenter(val schedulers: Schedulers, val entityRepository: EntityRepository) : BasePresenter<TimetableView>() {
    fun getTimetable() {
        compositeObservable.add(
                Observables.combineLatest(entityRepository.lessons, entityRepository.events)
                        .subscribeOn(schedulers.backgroundThread())
                        .observeOn(schedulers.mainThread())
                        .subscribe({
                            val lessonList = it.first
                            val eventList = it.second

                            val result = Timetable()

                            if (lessonList.isNotEmpty()) {

                                val lessons = lessonList.groupBy { it.date }
                                val events = eventList.groupBy { it.date }

                                val startDate: LocalDate = lessons.keys.min()!!


                                forDateRange(startDate, startDate + 7.days()) { date ->

                                    if (lessons[date] == null || lessons[date]?.isEmpty() == true) {
                                        result.put(date, null)
                                    } else {
                                        val lessonsForDate = lessons[date]!!

                                        val schoolDay = SchoolDay()

                                        val lessonNumbers = lessonsForDate.map { it.lessonNumber }
                                        val firstLessonNumber = lessonNumbers.min()!!
                                        val lastLessonNumber = lessonNumbers.max()!!

                                        for (lessonNumber in firstLessonNumber..lastLessonNumber) {
                                            val lesson = lessonsForDate.singleOrNull { it.lessonNumber == lessonNumber }
                                            val event = events[date]?.singleOrNull { it.lessonNumber == lessonNumber }

                                            val timetableLesson =
                                                    if (lesson == null) null
                                                    else TimetableLesson(lesson, event)

                                            schoolDay.put(lessonNumber, timetableLesson)
                                        }

                                        result.put(date, schoolDay)
                                    }
                                    view?.showTimetable(result)
                                }
                            }
                        }, { view?.showErrorDialog(it) })
        )
    }
}