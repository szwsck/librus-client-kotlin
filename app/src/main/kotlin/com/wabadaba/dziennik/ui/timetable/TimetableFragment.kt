package com.wabadaba.dziennik.ui.timetable

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.DetailsDialogBuilder
import com.wabadaba.dziennik.ui.ifNotNull
import com.wabadaba.dziennik.vo.Lesson
import com.wabadaba.dziennik.vo.Teacher
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_timetable.*
import org.joda.time.LocalDate
import javax.inject.Inject

class TimetableFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TimetableViewModel

    private var adapter: FlexibleAdapter<IFlexible<*>> = FlexibleAdapter(null)

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

        viewModel.timetableData.observe(this, Observer { timetableData ->
            if (timetableData != null && !timetableData.empty) {
                fragment_timetable_recyclerview.visibility = View.VISIBLE
                fragment_timetable_message.visibility = View.GONE

                val items = mutableListOf<IFlexible<*>>()

                for ((date, schoolDay) in timetableData) {
                    val header = LessonHeaderItem(date)
                    schoolDay?.entries
                            ?.map { (lessonNumber, timetableLesson) ->
                                if (timetableLesson == null) EmptyLessonItem(header, lessonNumber)
                                else LessonItem(header, timetableLesson)
                            }
                            ?.forEach { header.addSubItem(it) }
                    header.isExpanded = !date.isBefore(LocalDate.now())
                    items.add(header)
                }

                adapter = FlexibleAdapter(items)

                adapter.setDisplayHeadersAtStartUp(true)
                adapter.expandItemsAtStartUp()
                adapter.isAutoCollapseOnExpand = false
                adapter.isAutoScrollOnExpand = false

                adapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { position ->
                    val item = adapter.getItem(position)
                    if (item is LessonItem && !item.lesson.canceled) {
                        showDialog(item.lesson)
                        false
                    } else {
                        true
                    }
                }

                fragment_timetable_recyclerview.layoutManager = LinearLayoutManager(activity)
                fragment_timetable_recyclerview.adapter = adapter
            } else {
                fragment_timetable_recyclerview.visibility = View.GONE
                fragment_timetable_message.visibility = View.VISIBLE
                fragment_timetable_message.text = "Brak lekcji"
            }
        })
    }

    private fun showDialog(lesson: Lesson) {
        val ddb = DetailsDialogBuilder(activity)
                .withTitle(getString(R.string.lesson_details))

        val orgSubjectName = lesson.orgSubject?.name
        val subjectName = lesson.subject?.name

        if (subjectName != null) {
            if (orgSubjectName != null && orgSubjectName != subjectName) {
                val ssb = SpannableStringBuilder()
                        .append(orgSubjectName)
                        .append(" -> ")
                        .append(subjectName, StyleSpan(Typeface.BOLD), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                ddb.addStyledField(getString(R.string.subject), ssb)
            } else {
                ddb.addField(getString(R.string.subject), subjectName)
            }
        }

        val orgTeacherName = lesson.orgTeacher?.getFullName()
        val teacherName = lesson.teacher?.getFullName()

        if (teacherName != null) {
            if (orgTeacherName != null && orgTeacherName != teacherName) {
                val ssb = SpannableStringBuilder()
                        .append(orgTeacherName)
                        .append(" -> ")
                        .append(teacherName, StyleSpan(Typeface.BOLD), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                ddb.addStyledField(getString(R.string.teacher), ssb)
            } else {
                ddb.addField(getString(R.string.teacher), teacherName.toString())
            }
        }

        lesson.entry?.classroom?.name.ifNotNull { ddb.addField("Sala", it) }
        ddb.addField(getString(R.string.date), lesson.date.toString(getString(R.string.date_format_full)))
        ddb.addField(getString(R.string.time), lesson.hourFrom.toString("HH:mm") + " - " + lesson.hourTo.toString("HH:mm"))
        ddb.build().show()
    }

    override fun onResume() {
        super.onResume()
        adapter.ifNotNull { it.notifyItemRangeChanged(0, it.itemCount) }
    }

    private fun Teacher.getFullName(): CharSequence? {
        val sb = StringBuilder()
        val firstName = this.firstName
        val lastName = this.lastName

        return if (!(firstName == null && lastName == null)) {
            if (firstName != null) sb.append(firstName)
            if (firstName != null && lastName != null) sb.append(' ')
            if (lastName != null) sb.append(lastName)
            sb
        } else null
    }
}

