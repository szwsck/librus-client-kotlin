package com.wabadaba.dziennik.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.wabadaba.dziennik.R

class DetailsDialogBuilder(private val activity: Activity) {

    var title: String? = null
    private val fields = mutableListOf<Pair<CharSequence, CharSequence>>()

    fun withTitle(title: String): DetailsDialogBuilder {
        this.title = title
        return this
    }

    fun addStyledField(title: CharSequence?, value: CharSequence?): DetailsDialogBuilder {
        fields.add(Pair(title ?: "", value ?: ""))
        return this
    }

    /**
     * Automatically uses bold font on value
     */
    fun addField(title: String?, value: String?): DetailsDialogBuilder {
        val boldSpan = StyleSpan(Typeface.BOLD)
        val styledValue = if (value != null) {
            val ssb = SpannableStringBuilder(value)
            ssb.setSpan(boldSpan, 0, value.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            ssb
        } else ""
        fields.add(Pair(title ?: "", styledValue))
        return this
    }

    @SuppressLint("InflateParams")
    fun build(): MaterialDialog {
        val inflater = LayoutInflater.from(activity)
        val dialogLayout = inflater.inflate(R.layout.dialog_details, null)
        val rootView = dialogLayout.findViewById<ViewGroup>(R.id.dialog_details_root)
        for ((index, field) in fields.withIndex()) {
            val fieldLayout = inflater.inflate(R.layout.detail_field, rootView, false)
            fieldLayout.findViewById<TextView>(R.id.detail_field_title).text = field.first
            fieldLayout.findViewById<TextView>(R.id.detail_field_value).text = field.second
            rootView.addView(fieldLayout)
            if (index < fields.size - 1) {
                val dividerView = inflater.inflate(R.layout.dialog_details_divider, rootView, false)
                rootView.addView(dividerView)
            }
        }
        val builder = MaterialDialog.Builder(activity)
        if (title != null) builder.title(title!!)
        builder.positiveText(activity.getString(R.string.close))
        builder.customView(dialogLayout, true)
        return builder.build()
    }
}