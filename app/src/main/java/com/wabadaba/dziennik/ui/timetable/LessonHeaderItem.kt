package com.wabadaba.dziennik.ui.timetable

import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import org.joda.time.LocalDate

class LessonHeaderItem(val date: LocalDate) : AbstractHeaderItem<LessonHeaderItem.ViewHolder>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LessonHeaderItem

        if (date != other.date) return false

        return true
    }

    override fun hashCode() = date.hashCode()

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) = ViewHolder(view, adapter)

    override fun getLayoutRes() = R.layout.item_lesson_header

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.title.text = date.toString()
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val title: TextView = view.findViewById(R.id.item_lesson_header_title)
    }

}