package com.wabadaba.dziennik.ui.timetable

import android.graphics.Typeface
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.ui.*
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_lesson.view.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class LessonItem(header: LessonHeaderItem, timetableLesson: TimetableLesson)
    : AbstractSectionableItem<LessonItem.ViewHolder, LessonHeaderItem>(header) {

    val lesson = timetableLesson.lesson
    private val event = timetableLesson.event

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LessonItem

        if (lesson != other.lesson) return false

        return true
    }

    override fun hashCode(): Int = lesson.hashCode()

    override fun getLayoutRes(): Int = R.layout.item_lesson

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): ViewHolder = ViewHolder(view!!, adapter!!)

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        val context = holder!!.itemView.context
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val displayTime = prefs.getBoolean("timetable_display_times", false)

        holder.apply {
            number.text = lesson.lessonNumber.toString()
            title.text = lesson.subject?.name

            subtitle.text =
                    if (displayTime) "${lesson.hourFrom.toString("HH:mm")} - ${lesson.hourTo.toString("HH:mm")}"
                    else lesson.teacher?.fullName()

            classroom.text = lesson.entry?.classroom?.symbol

            if (lesson.canceled) {
                title.disabled()
                subtitle.disabled()
                number.disabled()
                classroom.disabled()

                badgeIcon.secondary().setImageResource(R.drawable.ic_cancel_black_24dp)
                badgeTitle.secondary().text = context.getString(R.string.canceled)

            } else {
                title.primary()
                subtitle.secondary()
                number.primary()
                classroom.secondary()

                when {
                    event != null -> {
                        badgeIcon.secondary().setImageResource(R.drawable.ic_event_black_24dp)
                        badgeTitle.secondary().text = event.category?.name
                    }
                    lesson.substitution -> {
                        badgeIcon.secondary().setImageResource(R.drawable.ic_swap_horiz_black_24dp)
                        badgeTitle.secondary().text = context.getString(R.string.substitution)
                    }
                    else -> {
                        badgeIcon.gone()
                        badgeTitle.gone()
                    }

                }
            }

            if (LocalDate.now() == lesson.date && LocalTime.now() in lesson.hourFrom..lesson.hourTo) {
                title.setTypeface(title.typeface, Typeface.BOLD)
            } else {
                title.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val number: TextView = view.item_lesson_number
        val title: TextView = view.item_lesson_title
        val subtitle: TextView = view.item_lesson_subtitle
        val classroom: TextView = view.item_lesson_classroom
        val badgeIcon: ImageView = view.item_lesson_badge_icon
        val badgeTitle: TextView = view.item_lesson_badge_title
    }
}