package com.wabadaba.dziennik.ui.announcements

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.base.BaseFragment
import com.wabadaba.dziennik.ui.*
import com.wabadaba.dziennik.ui.mainactivity.MainActivity
import com.wabadaba.dziennik.vo.Announcement
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.fragment_announcements.*
import javax.inject.Inject

class AnnouncementsFragment : BaseFragment(), AnnouncementView {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_announcements, container, false)

    @Inject lateinit var presenter : AnnouncementPresenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.subscribe(this)
        presenter.getAnnouncementData()
    }

    override fun showAnnouncements(announcementData: AnnouncementData) {
        if (announcementData.isNotEmpty()) {
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
            adapter.mItemClickListener = FlexibleAdapter.OnItemClickListener { _, position ->
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
    }
}