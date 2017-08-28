package com.wabadaba.dziennik.ui.timetable

import android.view.View
import com.wabadaba.dziennik.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class EmptyLessonItem(header: LessonHeaderItem) : AbstractSectionableItem<EmptyLessonItem.ViewHolder, LessonHeaderItem>(header) {

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = ViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.item_empty_lesson

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int, payloads: MutableList<Any?>?) {

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) : FlexibleViewHolder(view, adapter)
}