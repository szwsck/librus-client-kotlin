package com.wabadaba.dziennik.ui.grades

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.vo.Grade
import java.util.*

class GradeItem(val grade: Grade, var header: GradeHeaderItem)
    : AbstractItem<GradeItem, GradeItem.ViewHolder>(), ISubItem<GradeItem, GradeHeaderItem> {
    override fun getParent(): GradeHeaderItem = header

    override fun withParent(parent: GradeHeaderItem): GradeItem {
        header = parent
        return this
    }

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.grade_item

    override fun getType(): Int = R.id.grade_item_id

    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)

        holder.grade.text = grade.grade
        holder.title.text = grade.category?.name
        holder.subtitle.text = grade.date?.toString("EEEE, d MMMM", Locale("pl"))
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.grade_item_title)
        val subtitle: TextView = view.findViewById(R.id.grade_item_subtitle)
        val grade: TextView = view.findViewById(R.id.grade_item_grade)
    }

}

