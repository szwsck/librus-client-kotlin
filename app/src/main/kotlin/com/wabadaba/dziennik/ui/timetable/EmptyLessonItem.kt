package com.wabadaba.dziennik.ui.timetable

import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.ui.disabled
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_empty_lesson.view.*

class EmptyLessonItem(header: LessonHeaderItem, private val lessonNumber: Int)
    : AbstractSectionableItem<EmptyLessonItem.ViewHolder, LessonHeaderItem>(header) {


    override fun getLayoutRes(): Int = R.layout.item_empty_lesson

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.itemView.apply {
            item_empty_lesson_number.disabled().text = lessonNumber.toString()
            item_empty_lesson_message.disabled()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmptyLessonItem

        if (lessonNumber != other.lessonNumber) return false

        return true
    }

    override fun hashCode(): Int {
        return lessonNumber
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)
}