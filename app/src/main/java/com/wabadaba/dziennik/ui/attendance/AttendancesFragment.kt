package com.wabadaba.dziennik.ui.attendance

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_attendances.*
import javax.inject.Inject

class AttendancesFragment : LifecycleFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: AttendancesViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater?.inflate(R.layout.fragment_attendances, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainApplication = activity.application as MainApplication
        mainApplication.mainComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelFactory.create(AttendancesViewModel::class.java)
        viewModel.attendances.observe(this, Observer { attendances ->
            if (attendances == null) return@Observer     //if null, do nothing

            val items = mutableListOf<IFlexible<*>>()

            val attendanceMap = attendances.groupBy { it.date }
                    .toSortedMap(Comparator { o1, o2 -> o1?.compareTo(o2) ?: 0 })

            attendanceMap.forEach { entry ->
                val header = AttendanceHeaderItem(entry.key!!)
                entry.value.sortedBy { it.lessonNumber }
                        .map { AttendanceItem(it, header) }
                        .forEach(header::addSubItem)
                items.add(header)
            }

            val adapter = FlexibleAdapter(items)
            adapter.collapseAll()

            fragment_attendances_recyclerview.layoutManager = LinearLayoutManager(activity)
            fragment_attendances_recyclerview.itemAnimator = null
            fragment_attendances_recyclerview.adapter = adapter

        })
    }
}

