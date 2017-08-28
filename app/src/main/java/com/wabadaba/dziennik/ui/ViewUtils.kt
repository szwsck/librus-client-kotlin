package com.wabadaba.dziennik.ui

import android.view.View

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