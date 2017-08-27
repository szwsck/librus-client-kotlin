package com.wabadaba.dziennik.ui.grades

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.commons.items.AbstractExpandableItem
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.vo.Subject

class GradeHeaderItem(val subject: Subject) : AbstractExpandableItem<GradeHeaderItem, GradeHeaderItem.ViewHolder, GradeItem>() {

    var onExpandListener: FastAdapter.OnClickListener<GradeHeaderItem> = FastAdapter.OnClickListener {
        v, adapter, item, position ->
        if (item.subItems != null) {
            val subtitle = v.findViewById<TextView>(R.id.grade_header_item_subtitle)
            subtitle.text = if (item.isExpanded) {
                if (subject.average != null && subject.average!!.fullYear > 0) {
                    SpannableStringBuilder()
                            .append("Średnia: ")
                            .append(subject.average!!.fullYear.toString(), StyleSpan(Typeface.BOLD), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                } else {
                    "Brak średniej"
                }
            } else {
                getSubtitle(item.subItems.size)
            }
            mOnItemClickListener == null || mOnItemClickListener.onClick(v, adapter, item, position)
        }
        mOnItemClickListener != null && onItemClickListener.onClick(v, adapter, item, position)
    }

    override fun getOnItemClickListener() = onExpandListener

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun getType(): Int = R.id.grade_header_item_id

    override fun getLayoutRes(): Int = R.layout.grade_header_item

    @SuppressLint("SetTextI18n")
    override fun bindView(holder: ViewHolder, payloads: MutableList<Any>?) {
        super.bindView(holder, payloads)
        holder.title.text = subject.name

        holder.subtitle.text = if (isExpanded) {
            if (subject.average != null && subject.average!!.fullYear > 0) {
                SpannableStringBuilder()
                        .append("Średnia: ")
                        .append(subject.average!!.fullYear.toString(), StyleSpan(Typeface.BOLD), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            } else {
                "Brak średniej"
            }
        } else {
            getSubtitle(subItems.size)
        }
    }

    private fun getSubtitle(x: Int) = "$x " +
            if (x == 0) "ocen"
            else if (x == 1) "ocena"
            else if (x % 10 in 2..4 && x % 100 !in 12..14) "oceny"
            else "ocen"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.grade_header_item_title)
        val subtitle: TextView = view.findViewById(R.id.grade_header_item_subtitle)
    }
}