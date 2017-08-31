package com.wabadaba.dziennik.ui.attendance

import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import org.joda.time.LocalDate

class AttendanceHeaderItem(val date: LocalDate) : AbstractExpandableHeaderItem<AttendanceHeaderItem.ViewHolder, AttendanceItem>() {

    override fun getLayoutRes() = R.layout.item_attendance_header

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.title.text = date.toString("EEEE, d MMMM")
        holder.subtitle.text = getSubtitle(subItemsCount)
    }

    private fun getSubtitle(x: Int) = "$x " +
            if (x == 1) "nieobecność"
            else "nieobecności"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceHeaderItem

        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        return date.hashCode()
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : ExpandableViewHolder(view, adapter) {
        val title: TextView = view.findViewById(R.id.item_attendance_header_title)
        val subtitle: TextView = view.findViewById(R.id.item_attendance_header_subtitle)
    }
}
