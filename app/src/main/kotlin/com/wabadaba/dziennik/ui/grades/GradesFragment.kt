package com.wabadaba.dziennik.ui.grades

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.*
import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.Subject
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_grades.*
import mu.KotlinLogging
import org.joda.time.LocalDate
import java.util.*
import javax.inject.Inject


class GradesFragment : Fragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    @Inject lateinit var sharedPrefs: SharedPreferences

    private lateinit var viewModel: GradesViewModel

    private val logger = KotlinLogging.logger { }

    private lateinit var displayType: DisplayType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.mainComponent.inject(this)
        displayType = DisplayType.valueOf(sharedPrefs.getString("grade_display_type", "DATE"))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.fragment_grades, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel::class.java)

        viewModel.grades.observe(this, Observer { grades ->
            if (grades != null && grades.isNotEmpty()) {
                fragment_grades_recyclerview.visibility = View.VISIBLE
                fragment_grades_message.visibility = View.GONE
                displayGrades(grades)
            } else {
                fragment_grades_recyclerview.visibility = View.GONE
                fragment_grades_message.visibility = View.VISIBLE
                fragment_grades_message.text = getString(R.string.no_grades)
            }
        })

    }

    private fun displayGrades(grades: List<Grade>) {
        logger.info { "Displaying ${grades.size} grades" }

        val items = mutableListOf<IFlexible<*>>()

        when (displayType) {
            DisplayType.SUBJECT -> {

                val subjectGradeMap = mutableMapOf<Subject, MutableList<Grade>>()
                for (grade in grades) {
                    if (grade.subject != null && !subjectGradeMap.contains(grade.subject)) {
                        subjectGradeMap.put(grade.subject!!, mutableListOf())
                    }
                    subjectGradeMap[grade.subject]?.add(grade)
                }

                subjectGradeMap.entries
                        .forEach { entry: MutableMap.MutableEntry<Subject, MutableList<Grade>> ->
                            val header = GradeHeaderItem(entry.key)
                            entry.value.sortedByDescending(Grade::date)
                                    .map { GradeItem(it, header) }
                                    .forEach(header::addSubItem)
                            items.add(header)
                        }

            }
            DisplayType.DATE -> {
                val sections = TreeMap<DateHeader, List<Grade>>()
                grades.filter { it.date != null }
                        .forEach { grade ->
                            val date = grade.date!!
                            val header = when (date) {
                                LocalDate.now() ->
                                    DateHeader(0, "Dzisiaj")
                                LocalDate.now().minusDays(1) ->
                                    DateHeader(1, "Wczoraj")
                                in LocalDate.now().minusDays(LocalDate.now().dayOfWeek - 1)..LocalDate.now() ->
                                    DateHeader(2, "Ten tydzień")
                                in LocalDate.now().minusDays(LocalDate.now().dayOfMonth - 1)..LocalDate.now() ->
                                    DateHeader(3, "Ten miesiąc")
                                else -> DateHeader(3 + (LocalDate.now().monthOfYear - date.monthOfYear),
                                        date.monthNameNominative().capitalize())
                            }
                            sections.multiPut(header, grade)
                        }
                sections.entries.forEach { (sectionHeader, sectionGrades) ->
                    val headerItem = HeaderItem(sectionHeader.order, sectionHeader.title)
                    sectionGrades.sortedByDescending { it.date!! }
                            .forEach { items += GradeItem(it, headerItem) }
                }
            }
        }

        val adapter = FlexibleAdapter<IFlexible<*>>(items)
        adapter.setDisplayHeadersAtStartUp(true)
        adapter.collapseAll()

        adapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { position ->
            val item = adapter.getItem(position)
            if (item is GradeItem) {
                showDialog(item.grade)
                false
            } else {
                true
            }
        }
        fragment_grades_recyclerview.itemAnimator = null
        fragment_grades_recyclerview.layoutManager = LinearLayoutManager(activity)
        fragment_grades_recyclerview.adapter = adapter
    }

    private fun showDialog(grade: Grade) {
        val ddb = DetailsDialogBuilder(activity)
                .withTitle(getString(R.string.grade_details))

        if (grade.grade != null)
            ddb.addField(getString(R.string.grade), grade.grade)

        if (grade.category?.name != null)
            ddb.addField(getString(R.string.category), grade.category?.name)

        if (grade.category?.weight != null)
            ddb.addField(getString(R.string.weight), grade.category?.weight?.toString())

        if (grade.subject?.name != null)
            ddb.addField(getString(R.string.subject), grade.subject?.name)

        if (grade.date != null)
            ddb.addField(getString(R.string.date), grade.date?.toString(getString(R.string.date_format_full)))

        if (grade.addedBy?.fullName() != null)
            ddb.addField(getString(R.string.added_by), grade.addedBy?.fullName())

        for (comment in grade.comments) {
            ddb.addField(getString(R.string.comment), comment.text)
        }
        ddb.build().show()
    }
}

data class DateHeader(val order: Int, val title: String) : Comparable<DateHeader> {
    override fun compareTo(other: DateHeader) = order.compareTo(other.order)
}

enum class DisplayType {
    SUBJECT,
    DATE
}