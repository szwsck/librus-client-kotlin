package com.wabadaba.dziennik.ui.attendance

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.DetailsDialogBuilder
import com.wabadaba.dziennik.ui.gone
import com.wabadaba.dziennik.ui.visible
import com.wabadaba.dziennik.vo.Attendance
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_attendances.*
import javax.inject.Inject

class AttendancesFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: AttendancesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_attendances, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.mainComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelFactory.create(AttendancesViewModel::class.java)
        viewModel.attendances.observe(this, Observer { attendances ->
            if (attendances == null || attendances.isEmpty()) {
                fragment_attendances_recyclerview.gone()
                fragment_attendances_message.visible()
                return@Observer
            } else {
                fragment_attendances_recyclerview.visible()
                fragment_attendances_message.gone()
            }

            val items = mutableListOf<IFlexible<*>>()

            val attendanceMap = attendances.groupBy { it.date }
                    .toSortedMap(Comparator { o1, o2 -> o2?.compareTo(o1) ?: 0 })

            attendanceMap.forEach { entry ->
                val header = AttendanceHeaderItem(entry.key!!)
                entry.value.sortedBy { it.lessonNumber }
                        .map { AttendanceItem(it, header) }
                        .forEach(header::addSubItem)
                items.add(header)
            }

            val adapter = FlexibleAdapter(items)
            adapter.collapseAll()
            adapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { position ->
                val item = adapter.getItem(position)
                if (item is AttendanceItem) {
                    showDialog(item.attendance)
                    false
                } else {
                    true
                }
            }

            fragment_attendances_recyclerview.layoutManager = LinearLayoutManager(activity)
            fragment_attendances_recyclerview.itemAnimator = null
            fragment_attendances_recyclerview.adapter = adapter

        })
    }

    private fun showDialog(attendance: Attendance) {
        val ddb = DetailsDialogBuilder(activity as Activity)
                .withTitle(getString(R.string.absence_details))

        val typeName = attendance.type?.name
        if (typeName != null)
            ddb.addField(getString(R.string.type), typeName)

        val subject = attendance.lesson?.subject?.name
        if (subject != null)
            ddb.addField(getString(R.string.subject), subject)

        val date = attendance.date
        val lessonNumber = attendance.lessonNumber
        if (date != null && lessonNumber != null)
            ddb.addField(getString(R.string.date), "Lekcja $lessonNumber, ${date.toString(context?.getString(R.string.date_format_full))}")

        val addedBy = attendance.addedBy
        if (addedBy?.firstName != null && addedBy.lastName != null)
            ddb.addField(getString(R.string.added_by), "${addedBy.firstName} ${addedBy.lastName}")

        val addDate = attendance.addDate
        if (addDate != null)
            ddb.addField(getString(R.string.date_added), addDate.toString(context?.getString(R.string.date_format_full) + " HH:mm:ss"))

        ddb.build().show()
    }
}

