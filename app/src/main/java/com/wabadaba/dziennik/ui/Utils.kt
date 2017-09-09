package com.wabadaba.dziennik.ui

import android.view.View
import com.github.debop.kodatimes.days
import org.joda.time.LocalDate
import java.util.*

fun <T : View> T.visible(): T {
    this.visibility = View.VISIBLE
    return this
}

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

fun <K, V> TreeMap<K, List<V>>.multiPut(key: K, value: V) {
    if (!this.containsKey(key))
        this.put(key, kotlin.collections.emptyList())
    val newList = this[key]!!.plus(value)
    this.put(key, newList)
}

fun LocalDate.monthNameNominative() =
        when (this.monthOfYear) {
            1 -> "styczeń"
            2 -> "luty"
            3 -> "marzec"
            4 -> "kwiecień"
            5 -> "maj"
            6 -> "czerwiec"
            7 -> "lipiec"
            8 -> "sierpień"
            9 -> "wrzesień"
            10 -> "październik"
            11 -> "listopad"
            12 -> "grudzień"
            else -> throw IllegalArgumentException("Invalid date $this")
        }

fun forDateRange(dateStart: LocalDate, dateEnd: LocalDate, consumer: (LocalDate) -> Unit) {
    var date = dateStart

    while (date < dateEnd) {
        consumer.invoke(date)
        date += 1.days()
    }
}

fun weekEnd(): LocalDate = LocalDate.now().dayOfWeek().withMaximumValue()

fun monthEnd(): LocalDate = LocalDate.now().dayOfMonth().withMaximumValue()

fun weekStart(): LocalDate = LocalDate.now().dayOfWeek().withMinimumValue()

fun monthStart(): LocalDate = LocalDate.now().dayOfMonth().withMinimumValue()