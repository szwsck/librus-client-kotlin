package com.wabadaba.dziennik.ui.grades

import android.graphics.Color
import android.view.View
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.vo.Grade
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.IHeader
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.grade_item.view.*
import java.util.*

class GradeItem(val grade: Grade, header: IHeader<*>)
    : AbstractSectionableItem<GradeItem.ViewHolder, IHeader<*>>(header) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeItem

        if (grade != other.grade) return false

        return true
    }

    override fun hashCode(): Int {
        return grade.hashCode()
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = ViewHolder(view, adapter)

    override fun getLayoutRes(): Int = R.layout.grade_item

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.itemView.apply {
            grade_item_grade.text = grade.grade
            grade_item_title.text = grade.category?.name
            grade_item_subtitle.text = grade.date?.toString("EEEE, d MMMM", Locale("pl"))
            val rawColor = grade.category?.color?.rawColor
            val colorInt =
                    if (rawColor != null) Color.parseColor('#' + rawColor)
                    else Color.TRANSPARENT
            grade_item_color.setBackgroundColor(colorInt)
        }
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)

}

