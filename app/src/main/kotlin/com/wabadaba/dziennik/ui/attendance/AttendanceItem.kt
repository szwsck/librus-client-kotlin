package com.wabadaba.dziennik.ui.attendance

import android.view.View
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.vo.Attendance
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_attendance.view.*

class AttendanceItem(val attendance: Attendance, header: AttendanceHeaderItem)
    : AbstractSectionableItem<AttendanceItem.ViewHolder, AttendanceHeaderItem>(header) {

    override fun getLayoutRes() = R.layout.item_attendance

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>)
            = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.itemView.apply {
            item_attendance_name_short.text = attendance.type?.shortName ?: ""
            item_attendance_title.text = attendance.lesson?.subject?.name ?: ""
            item_attendance_subtitle.text = if (attendance.lessonNumber != null) "lekcja ${attendance.lessonNumber!!}" else ""
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceItem

        if (attendance != other.attendance) return false

        return true
    }

    override fun hashCode(): Int {
        return attendance.hashCode()
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)
}


