package com.wabadaba.dziennik.ui.grades

import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.vo.Grade
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class GradeItem(val grade: Grade) : AbstractFlexibleItem<GradeItem.ViewHolder>() {

    override fun equals(other: Any?): Boolean = grade == other

    override fun hashCode() = grade.hashCode()

    override fun getLayoutRes(): Int = R.layout.grade_item

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ViewHolder
            = ViewHolder(view, adapter)

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val title: TextView = view.findViewById(R.id.grade_item_title)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.title.text = grade.grade
    }


}