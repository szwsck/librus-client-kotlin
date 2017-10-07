package com.wabadaba.dziennik.ui

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class HeaderItem(val order: Int = 0, val title: String) : AbstractHeaderItem<HeaderItem.ViewHolder>(), Comparable<HeaderItem> {

    override fun getLayoutRes() = R.layout.item_header

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.title.text = SpannableStringBuilder()
                .append(title, StyleSpan(Typeface.BOLD), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val title: TextView = view.findViewById(R.id.item_header_title)
    }

    override fun compareTo(other: HeaderItem) = order.compareTo(other.order)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HeaderItem

        if (order != other.order) return false
        if (title != other.title) return false

        return true
    }

    override fun hashCode(): Int {
        var result = order
        result = 31 * result + title.hashCode()
        return result
    }


}