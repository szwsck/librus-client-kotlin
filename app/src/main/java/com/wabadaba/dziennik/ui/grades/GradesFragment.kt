package com.wabadaba.dziennik.ui.grades

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import com.wabadaba.dziennik.vo.Grade
import com.wabadaba.dziennik.vo.Subject
import kotlinx.android.synthetic.main.fragment_grades.*
import mu.KotlinLogging
import javax.inject.Inject


class GradesFragment : LifecycleFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    val logger = KotlinLogging.logger { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainApplication = activity.application as MainApplication
        mainApplication.mainComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater?.inflate(R.layout.fragment_grades, container, false)

    private lateinit var viewModel: GradesViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel::class.java)

        viewModel.grades.observe(this, Observer { grades ->
            if (grades != null) {
                logger.info { "Displaying ${grades.size} grades" }
                val subjectGradeMap = mutableMapOf<Subject, MutableList<Grade>>()
                for (grade in grades) {
                    if (!subjectGradeMap.contains(grade.subject)) {
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
                recycler_view_grades.adapter = adapter
                recycler_view_grades.layoutManager = LinearLayoutManager(activity)
                adapter.add(headers)
            }
        })
    }
}