package com.wabadaba.dziennik.ui.grades

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wabadaba.dziennik.api.EntityRepository
import com.wabadaba.dziennik.vo.Grade
import io.reactivex.android.schedulers.AndroidSchedulers

class GradesViewModel(entityRepo: EntityRepository) : ViewModel() {

    val grades = MutableLiveData<List<Grade>>()

    init {
        entityRepo.grades
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { grades.value = it }
        entityRepo.averages
                .subscribe { System.out.println("Got ${it.size} averages") }
    }
}