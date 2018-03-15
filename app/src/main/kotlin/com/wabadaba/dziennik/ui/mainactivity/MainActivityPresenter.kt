package com.wabadaba.dziennik.ui.mainactivity

import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.api.UserRepository
import com.wabadaba.dziennik.base.BasePresenter
import com.wabadaba.dziennik.base.Schedulers
import com.wabadaba.dziennik.ui.GPServicesChecker
import com.wabadaba.dziennik.vo.LuckyNumber

class MainActivityPresenter(val schedulers: Schedulers,
                            val userRepository: UserRepository,
                            val entityRepository : EntityRepository) : BasePresenter<MainActivityView>() {
    fun getUsers() {
        compositeObservable.add(
                userRepository.allUsers
                        .observeOn(schedulers.backgroundThread())
                        .subscribe({ view?.initUsers(it) }, { view?.showErrorDialog(it) })
        )
    }

    fun getLuckyNumber() {
        compositeObservable.add(
                entityRepository.luckyNumber
                        .observeOn(schedulers.backgroundThread())
                        .subscribe({
                            val luckyNumber = it.sortedBy(LuckyNumber::date).reversed().firstOrNull()
                            view?.addLuckyNumberSection(luckyNumber)
                        }, { view?.showErrorDialog(it) })
        )
    }
}