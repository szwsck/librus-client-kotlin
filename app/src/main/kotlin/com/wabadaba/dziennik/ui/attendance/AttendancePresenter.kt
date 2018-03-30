package com.wabadaba.dziennik.ui.attendance

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.BasePresenter
import com.wabadaba.dziennik.base.Schedulers

class AttendancePresenter(val schedulers: Schedulers, val entityRepository: EntityRepository) : BasePresenter<AttendanceView>() {
    fun getAttendance() {
        compositeObservable.add(
            entityRepository.attendances
                    .subscribeOn(schedulers.backgroundThread())
                    .observeOn(schedulers.mainThread())
                    .subscribe({view?.showAttendance(it)}, { view?.showErrorDialog(it) })
        )
    }
}