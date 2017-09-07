package com.wabadaba.dziennik.ui.events

import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.vo.Event
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class EventItem(private val event: Event) : AbstractFlexibleItem<EventItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_event

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.title.text = event.category?.name
        holder.subtitle.text =
                if (event.lessonNumber != null) "Lekcja ${event.lessonNumber}"
                else ""
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val title: TextView = view.findViewById(R.id.item_event_title)
        val subtitle: TextView = view.findViewById(R.id.item_event_subtitle)
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