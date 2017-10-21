package com.wabadaba.dziennik.ui.announcements

import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.ui.HeaderItem
import com.wabadaba.dziennik.ui.fullName
import com.wabadaba.dziennik.vo.Announcement
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_announcement.view.*

class AnnouncementItem(val announcement: Announcement, header: HeaderItem) : AbstractSectionableItem<AnnouncementItem.ViewHolder, HeaderItem>(header), Comparable<AnnouncementItem> {

    override fun getLayoutRes() = R.layout.item_announcement

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = AnnouncementItem.ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.itemView.apply {
            item_announcement_title.text = announcement.title
            item_announcement_addedBy.text = announcement.addedBy?.fullName()
        }
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)

    override fun compareTo(other: AnnouncementItem): Int {
        if (announcement.addDate != null && other.announcement.addDate != null) {
            val compare1 = announcement.addDate!!.compareTo(other.announcement.addDate!!)
            if (compare1 == 0) {
                if (announcement.startDate != null && other.announcement.startDate != null) {
                    val compare2 = announcement.startDate!!.compareTo(other.announcement.startDate!!)
                    if (compare2 == 0) {
                        if (announcement.endDate != null && other.announcement.endDate != null) {
                            return announcement.endDate!!.compareTo(other.announcement.endDate!!)
                        }
                    }
                }
            } else return compare1
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (javaClass != other?.javaClass) return false

        other as AnnouncementItem

        if (announcement != other.announcement) return false

        return true
    }

    override fun hashCode(): Int {
        return announcement.hashCode()
    }
}