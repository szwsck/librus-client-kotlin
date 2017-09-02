package com.wabadaba.dziennik.ui

import android.view.View
import com.github.debop.kodatimes.days
import org.joda.time.LocalDate

fun <T : View> T.primary(): T {
    this.visibility = View.VISIBLE
    this.alpha = 0.87F
    return this
}

fun <T : View> T.secondary(): T {
    this.visibility = View.VISIBLE
    this.alpha = 0.54F
    return this
}

fun <T : View> T.disabled(): T {
    this.visibility = View.VISIBLE
    this.alpha = 0.38F
    return this
}

fun <T : View> T.gone(): T {
    this.visibility = View.GONE
    return this
}

fun forDateRange(dateStart: LocalDate, dateEnd: LocalDate, consumer: (LocalDate) -> Unit) {
    var date = dateStart

    while (date < dateEnd) {
        consumer.invoke(date)
        date += 1.days()
    }
}

fun dateRange(dateStart: LocalDate, dateEnd: LocalDate): Iterable<LocalDate> {
    var date = dateStart
    val result = mutableListOf<LocalDate>()

    while (date < dateEnd) {
        result.add(date)
        date += 1.days()
    }
    return result
}