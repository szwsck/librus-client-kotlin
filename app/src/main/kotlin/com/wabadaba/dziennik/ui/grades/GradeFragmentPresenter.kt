package com.wabadaba.dziennik.ui.grades

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.base.BasePresenter
import com.wabadaba.dziennik.base.Schedulers

class GradeFragmentPresenter(val schedulers: Schedulers, val entityRepository: EntityRepository) : BasePresenter<GradeFragmentView>() {
    fun getGrades() {
        compositeObservable.add(
                entityRepository.grades.subscribeOn(schedulers.backgroundThread())
                        .observeOn(schedulers.mainThread())
                        .subscribe({ view?.showGrades(it) }, { view?.showErrorDialog(it) })
        )
    }
}