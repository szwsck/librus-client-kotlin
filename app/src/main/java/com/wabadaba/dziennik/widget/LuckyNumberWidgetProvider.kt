package com.wabadaba.dziennik.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.vo.LuckyNumber
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


/**
 * Created by xdk78 on 2017-09-09.
 */
class LuckyNumberWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var entityRepository: EntityRepository
    lateinit var luckyNumber: LuckyNumber

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        appWidgetIds?.forEach { id ->
            val widget = RemoteViews(context?.packageName, R.layout.widget_lucky_number)

            entityRepository.luckyNumber.observeOn(AndroidSchedulers.mainThread())
                    .map { luckyNumbers -> luckyNumbers.sortedBy(LuckyNumber::date).firstOrNull() }
                    .subscribe {luckyNumber = it!! }
            if (luckyNumber.number != null) {
                widget.setTextViewText(R.id.lucky_number_date, luckyNumber.date!!.toString("EEEE, d MMMM"))
                widget.setTextViewText(R.id.lucky_number, luckyNumber.number.toString())
                appWidgetManager?.updateAppWidget(id, widget)
            }
        }


    }


}