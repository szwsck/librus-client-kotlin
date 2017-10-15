package com.wabadaba.dziennik.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent


/**
 * Created by xdk78 on 2017-09-09.
 */
class LuckyNumberWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val intentService = Intent(context, LuckyNumberWidgetService::class.java)
            intentService.action = "refresh"
            intentService.putExtra("widgetId", appWidgetId)
            context.startService(intentService)
        }
    }
}