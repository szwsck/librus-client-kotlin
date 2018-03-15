package com.wabadaba.dziennik.base

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

class ApplicationSchedulers : Schedulers {
    override fun backgroundThread(): Scheduler {
        return io.reactivex.schedulers.Schedulers.io()
    }

    override fun mainThread(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}