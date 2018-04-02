package com.wabadaba.dziennik.ui.events

import android.support.v7.widget.RecyclerView
import android.view.View
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.ui.HeaderItem
import com.wabadaba.dziennik.vo.Event
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_event.view.*
import org.joda.time.LocalDate

class EventItem(val event: Event, header: HeaderItem) : AbstractSectionableItem<EventItem.ViewHolder, HeaderItem>(header), Comparable<EventItem> {

    override fun getLayoutRes() = R.layout.item_event

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): ViewHolder = ViewHolder(view!!, adapter!!)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        val date = event.date!!
        holder!!.itemView.apply {
            item_event_title.text = event.category?.name
            item_event_subtitle.text =
                    when (date) {
                        LocalDate.now(), LocalDate.now().plusDays(1) -> {
                            if (event.lessonNumber != null)
                                "lekcja ${event.lessonNumber}"
                            else ""
                        }
                        else -> {
                            val dateFormat = context.getString(R.string.date_format_full)
                            date.toString(dateFormat)
                        }
                    }
        }

    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)

    override fun compareTo(other: EventItem): Int {
        if (event.date != null && other.event.date != null) {
            val compare1 = event.date!!.compareTo(other.event.date!!)
            if (compare1 == 0) {
                if (event.lessonNumber != null && other.event.lessonNumber != null) {
                    return event.lessonNumber!!.compareTo(other.event.lessonNumber!!)
                }
            } else return compare1
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventItem

        if (event != other.event) return false

        return true
    }

    override fun hashCode(): Int {
        return event.hashCode()
    }
}