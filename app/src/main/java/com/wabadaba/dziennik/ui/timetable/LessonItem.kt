package com.wabadaba.dziennik.ui.timetable

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.ui.disabled
import com.wabadaba.dziennik.ui.gone
import com.wabadaba.dziennik.ui.primary
import com.wabadaba.dziennik.ui.secondary
import com.wabadaba.dziennik.vo.Lesson
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class LessonItem(header: LessonHeaderItem, private val lesson: Lesson)
    : AbstractSectionableItem<LessonItem.ViewHolder, LessonHeaderItem>(header) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LessonItem

        if (lesson != other.lesson) return false

        return true
    }

    override fun hashCode(): Int {
        return lesson.hashCode()
    }

    override fun getLayoutRes(): Int = R.layout.item_lesson

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>) = ViewHolder(view, adapter)

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {

        val context = holder.itemView.context

        holder.number.text = lesson.lessonNumber.toString()
        holder.title.text = lesson.subject?.name

        val firstName = lesson.teacher?.firstName
        val lastName = lesson.teacher?.lastName
        val sb = StringBuilder()

        if (!(firstName == null && lastName == null)) {
            if (firstName != null) sb.append(firstName)
            if (firstName != null && lastName != null) sb.append(' ')
            if (lastName != null) sb.append(lastName)
            holder.subtitle.text = sb
        } else {
            holder.subtitle.text = null
        }

        if (lesson.canceled) {

            holder.title.disabled()
            holder.subtitle.disabled()
            holder.number.disabled()

            holder.badgeIcon.secondary().setImageResource(R.drawable.ic_cancel_black_24dp)
            holder.badgeTitle.secondary().text = context.getString(R.string.canceled)

        } else {

            holder.title.primary()
            holder.subtitle.secondary()
            holder.number.primary()

            if (lesson.substitution) {
                holder.badgeIcon.secondary().setImageResource(R.drawable.ic_swap_horiz_black_24dp)
                holder.badgeTitle.secondary().text = context.getString(R.string.substitution)
            } else {
                holder.badgeIcon.gone()
                holder.badgeTitle.gone()
            }
        }
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val number: TextView = view.findViewById(R.id.item_lesson_number)
        val title: TextView = view.findViewById(R.id.item_lesson_title)
        val subtitle: TextView = view.findViewById(R.id.item_lesson_subtitle)
        val badgeIcon: ImageView = view.findViewById(R.id.item_lesson_badge_icon)
        val badgeTitle: TextView = view.findViewById(R.id.item_lesson_badge_title)
    }
}