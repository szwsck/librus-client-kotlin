package com.wabadaba.dziennik.ui.timetable

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import org.joda.time.LocalDate
import java.util.*

class LessonHeaderItem(val date: LocalDate)
    : AbstractExpandableHeaderItem<LessonHeaderItem.ViewHolder, AbstractSectionableItem<*, *>>() {

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
        holder.title.text = getDateText()
    }

    private fun getDateText(): SpannableStringBuilder {
        val title: String = when (date) {
            LocalDate.now() -> "Dzisiaj"
            LocalDate.now().plusDays(1) -> "Jutro"
            else -> date.toString("EEEE", Locale("pl"))
        }
        val subtitle = date.toString("d.M")

        return SpannableStringBuilder()
                .append(title.capitalize(), StyleSpan(Typeface.BOLD), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                .append(' ')
                .append(subtitle)
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : ExpandableViewHolder(view, adapter) {
        val title: TextView = view.findViewById(R.id.item_lesson_header_title)
    }

}