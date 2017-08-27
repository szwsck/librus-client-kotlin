package com.wabadaba.dziennik.ui.timetable

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_timetable.*
import javax.inject.Inject

class TimetableFragment : LifecycleFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: TimetableViewModel

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

        viewModel.lessons.observe(this, Observer { lessons ->
            if (lessons != null && lessons.isNotEmpty()) {
                fragment_timetable_message.visibility = View.GONE
                fragment_timetable_message.visibility = View.VISIBLE
                fragment_timetable_message.text = "${lessons.size} lekcji"
            } else {
                fragment_timetable_message.visibility = View.GONE
                fragment_timetable_message.visibility = View.VISIBLE
                fragment_timetable_message.text = "Brak lekcji"
            }
        })
    }
}