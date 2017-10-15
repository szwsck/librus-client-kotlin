package com.wabadaba.dziennik.widget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import com.wabadaba.dziennik.MainApplication
import com.wabadaba.dziennik.R
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.vo.LuckyNumber
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by xdk78 on 2017-10-14.
 */
class LuckyNumberWidgetService : Service() {

    private val TAG = LuckyNumberWidgetService::class.java.simpleName

    @Inject
    lateinit var entityRepository: EntityRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val mainApplication = applicationContext as MainApplication
        mainApplication.mainComponent.inject(this)

        if (intent != null && intent.hasExtra("widgetId")) {
            val appWidgetId = intent.getIntExtra("widgetId", 0)
            Log.d(TAG, "onStartCommand($appWidgetId)")
            updateWidget(appWidgetId)
        } else {
            Log.e(TAG, "onStartCommand(<no widgetId>)")
        }

        return Service.START_NOT_STICKY
    }

    private fun updateWidget(widgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val remoteViews = RemoteViews(application.packageName, R.layout.widget_lucky_number)
        remoteViews.setTextViewText(R.id.lucky_number_date, "Ładowanie...")
        appWidgetManager.updateAppWidget(widgetId, remoteViews)

        entityRepository.luckyNumber.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { luckyNumbers ->
                    val luckyNumber = luckyNumbers.sortedBy(LuckyNumber::date).reversed().firstOrNull()
                    when {
                        luckyNumber?.date != null && luckyNumber.number != null -> {
                            Log.i(TAG, "date: ${luckyNumber.date!!.toString("EEEE, d MMMM")}, number: ${luckyNumber.number}")
                            with(remoteViews, {
                                setTextViewText(
                                        R.id.lucky_number_date,
                                        luckyNumber.date!!.toString("EEEE, d MMMM")
                                )
                                setTextViewText(
                                        R.id.lucky_number,
                                        luckyNumber.number.toString()
                                )
                            })
                            appWidgetManager.updateAppWidget(widgetId, remoteViews)
                        }
                    }
                }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}