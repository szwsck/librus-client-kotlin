package com.wabadaba.dziennik.ui.grades

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.vo.Subject
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder

class GradeHeaderItem(val subject: Subject)
    : AbstractExpandableHeaderItem<GradeHeaderItem.ViewHolder, GradeItem>() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeHeaderItem

        if (subject != other.subject) return false

        return true
    }

    override fun hashCode(): Int {
        return subject.hashCode()
    }

    override fun getLayoutRes() = R.layout.grade_header_item

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.title.text = subject.name
        if (isExpanded) {
            holder.subtitle.text =
                    if (subject.average != null && subject.average!!.fullYear > 0) {
                        SpannableStringBuilder()
                                .append("Średnia: ")
                                .append(subject.average!!.fullYear.toString(), StyleSpan(Typeface.BOLD), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    } else {
                        "Brak średniej"
                    }
        } else {
            holder.subtitle.text = getSubtitle(subItemsCount)
        }
    }

    private fun getSubtitle(x: Int) = "$x " +
            if (x == 0) "ocen"
            else if (x == 1) "ocena"
            else if (x % 10 in 2..4 && x % 100 !in 12..14) "oceny"
            else "ocen"

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : ExpandableViewHolder(view, adapter) {
        val title: TextView = view.findViewById(R.id.grade_header_item_title)
        val subtitle: TextView = view.findViewById(R.id.grade_header_item_subtitle)
        override fun shouldNotifyParentOnClick() = true
    }
}