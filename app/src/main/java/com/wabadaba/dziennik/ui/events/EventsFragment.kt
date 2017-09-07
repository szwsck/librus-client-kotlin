package com.wabadaba.dziennik.ui.events

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
import kotlinx.android.synthetic.main.fragment_events.*
import javax.inject.Inject

class EventsFragment : LifecycleFragment() {
    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: EventsViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater?.inflate(R.layout.fragment_events, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainApplication = activity.application as MainApplication
        mainApplication.mainComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelFactory.create(EventsViewModel::class.java)
        viewModel.eventsData.observe(this, Observer { events ->
            if (events != null) {
                val items = mutableListOf<IFlexible<*>>()
                items.addAll(events.map(::EventItem))
                val adapter = FlexibleAdapter(items)

                fragment_events_recyclerview.layoutManager = LinearLayoutManager(activity)
                fragment_events_recyclerview.adapter = adapter
            }
        })

    }

}