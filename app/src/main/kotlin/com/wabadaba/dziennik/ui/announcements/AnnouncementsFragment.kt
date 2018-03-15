package com.wabadaba.dziennik.ui.announcements

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
import com.wabadaba.dziennik.ui.*
import com.wabadaba.dziennik.ui.mainactivity.MainActivity
import com.wabadaba.dziennik.vo.Announcement
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_announcements.*
import javax.inject.Inject

class AnnouncementsFragment : Fragment() {
    @Inject lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: AnnouncementsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_announcements, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.mainComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = viewModelFactory.create(AnnouncementsViewModel::class.java)
        viewModel.announcementData.observe(this, Observer { announcementData ->
            if (announcementData != null && announcementData.isNotEmpty()) {
                fragment_announcements_message.gone()
                fragment_announcements_recyclerview.visible()

                val items = mutableListOf<IFlexible<*>>()

                announcementData.entries.forEach { (header, announcements) ->
                    val headerItem = HeaderItem(header.order, header.title)
                    val sectionItems = announcements.map { AnnouncementItem(it, headerItem) }
                            .sorted()
                    items.addAll(sectionItems)
                }

                val adapter = FlexibleAdapter(items)
                adapter.setDisplayHeadersAtStartUp(true)
                adapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { position ->
                    val item = adapter.getItem(position)
                    if (item is AnnouncementItem) showDetailsDialog(item.announcement)
                    false
                }

                fragment_announcements_recyclerview.layoutManager = LinearLayoutManager(activity)
                fragment_announcements_recyclerview.adapter = adapter
            } else {
                fragment_announcements_message.visible()
                fragment_announcements_recyclerview.gone()
            }
        })
    }

    private fun showDetailsDialog(announcement: Announcement) {
        val dateTimeFormat = activity?.getString(R.string.date_format_full) + ' ' + getString(R.string.timeFormat)

        val ddb = DetailsDialogBuilder(activity as MainActivity)
                .withTitle("Szczegóły ogłoszenia")

        if (announcement.title != null)
            ddb.addField("Tytuł", announcement.title)
        if (announcement.content != null)
            ddb.addField("Treść", announcement.content)
        if (announcement.addedBy != null)
            ddb.addField("Dodane przez", announcement.addedBy?.fullName())
        if (announcement.addDate != null)
            ddb.addField("Data dodania", announcement.addDate?.toString(dateTimeFormat))

        ddb.build().show()
    }
}