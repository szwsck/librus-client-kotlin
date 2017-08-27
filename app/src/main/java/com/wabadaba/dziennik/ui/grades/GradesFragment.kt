package com.wabadaba.dziennik.ui.grades

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.ui.DetailsDialogBuilder
import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.Subject
import kotlinx.android.synthetic.main.fragment_grades.*
import mu.KotlinLogging
import javax.inject.Inject


class GradesFragment : LifecycleFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: GradesViewModel

    val logger = KotlinLogging.logger { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainApplication = activity.application as MainApplication
        mainApplication.mainComponent.inject(this)
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
        val subjectGradeMap = mutableMapOf<Subject, MutableList<Grade>>()
        for (grade in grades) {
            if (grade.subject != null && !subjectGradeMap.contains(grade.subject)) {
                subjectGradeMap.put(grade.subject!!, mutableListOf())
            }
            subjectGradeMap[grade.subject]?.add(grade)
        }
        val headers = subjectGradeMap.map {
            val header = GradeHeaderItem(it.key)
            val subItems = it.value.map { GradeItem(it, header) }
            header.withSubItems(subItems)
            header
        }
        val adapter = FastItemAdapter<IItem<*, *>>()
        adapter.withPositionBasedStateManagement(false)
        adapter.withSelectable(true)
        adapter.withOnClickListener({ _, _, item, _ ->
            if (item is GradeItem) {
                showDialog(item.grade)
                return@withOnClickListener true
            }
            return@withOnClickListener false
        })
        fragment_grades_recyclerview.adapter = adapter
        fragment_grades_recyclerview.itemAnimator = null
        fragment_grades_recyclerview.layoutManager = LinearLayoutManager(activity)
        adapter.add(headers)
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

        val addedBy = StringBuilder()
        val firstName = grade.addedBy?.firstName
        val lastName = grade.addedBy?.lastName

        if (!(firstName == null && lastName == null)) {
            if (firstName != null) addedBy.append(firstName)
            if (firstName != null && lastName != null) addedBy.append(' ')
            if (lastName != null) addedBy.append(lastName)
            ddb.addField(getString(R.string.added_by), addedBy.toString())
        }

        for (comment in grade.comments) {
            ddb.addField(getString(R.string.comment), comment.text)
        }
        ddb.build().show()
    }
}