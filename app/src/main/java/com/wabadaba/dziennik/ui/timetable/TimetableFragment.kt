package com.wabadaba.dziennik.ui.timetable

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.debop.kodatimes.days
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.vo.Lesson
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_timetable.*
import org.joda.time.LocalDate
import javax.inject.Inject

class TimetableFragment : LifecycleFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TimetableViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainApplication = activity.application as MainApplication
        mainApplication.mainComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.fragment_timetable, container, false)

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TimetableViewModel::class.java)

        viewModel.lessons.observe(this, Observer { lessonMap ->
            if (lessonMap != null && lessonMap.isNotEmpty()) {
                fragment_timetable_recyclerview.visibility = View.VISIBLE
                fragment_timetable_message.visibility = View.GONE

                val items = mutableListOf<IFlexible<*>>()

                val weekStart: LocalDate = lessonMap.keys.min()!!
                val endDate = weekStart + 7.days()
                var date = weekStart

                while (date < endDate) {
                    val header = LessonHeaderItem(date)
                    if (lessonMap.containsKey(date)) {
                        val schoolDay: List<Lesson> = lessonMap[date]!!
                        schoolDay.map { LessonItem(header, it) }
                                .forEach { items.add(it) }
                    } else {
                        items.add(EmptyLessonItem(header))
                    }
                    date += 1.days()
                }

                val adapter = FlexibleAdapter(items)
                adapter.setDisplayHeadersAtStartUp(true)

                fragment_timetable_recyclerview.itemAnimator = null
                fragment_timetable_recyclerview.layoutManager = LinearLayoutManager(activity)
                fragment_timetable_recyclerview.adapter = adapter

            } else {
                fragment_timetable_recyclerview.visibility = View.GONE
                fragment_timetable_message.visibility = View.VISIBLE
                fragment_timetable_message.text = "Brak lekcji"
            }
        })
    }
}

