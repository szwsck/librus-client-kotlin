package com.wabadaba.dziennik.base

import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {
    val supportFragmentManager by lazy { (activity as BaseActivity).supportFragmentManager }
    fun showErrorDialog(e : Throwable) {
    }
}