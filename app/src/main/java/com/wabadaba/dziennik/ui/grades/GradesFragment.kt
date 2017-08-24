package com.wabadaba.dziennik.ui.grades

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import eu.davidea.flexibleadapter.FlexibleAdapter
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

        recycler_view_grades.layoutManager = LinearLayoutManager(activity)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel::class.java)

        viewModel.grades.observe(this, Observer { grades ->
            logger.info { "Displaying ${grades?.size} grades" }
            val listItems = grades?.map(::GradeItem)
            recycler_view_grades.adapter = FlexibleAdapter(listItems)
        })
    }
}