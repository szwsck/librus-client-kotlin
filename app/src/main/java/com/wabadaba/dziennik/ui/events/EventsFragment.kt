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
import com.wabadaba.dziennik.ui.HeaderItem
import com.wabadaba.dziennik.ui.gone
import com.wabadaba.dziennik.ui.visible
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
        viewModel.eventData.observe(this, Observer { eventData ->
            if (eventData != null && eventData.isNotEmpty()) {
                fragment_events_message.gone()
                fragment_events_recyclerview.visible()

                val items = mutableListOf<IFlexible<*>>()

                eventData.entries.forEach { (header, events) ->
                    val headerItem = HeaderItem(header.order, header.title)
                    val sectionItems = events.map { EventItem(it, headerItem) }
                            .sorted()
                    items.addAll(sectionItems)
                }

                val adapter = FlexibleAdapter(items)
                adapter.setDisplayHeadersAtStartUp(true)

                fragment_events_recyclerview.layoutManager = LinearLayoutManager(activity)
                fragment_events_recyclerview.adapter = adapter
            } else {
                fragment_events_message.visible()
                fragment_events_recyclerview.gone()
            }
        })

    }

}